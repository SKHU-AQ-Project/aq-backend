package com.example.aq.global.security;

import com.example.aq.global.oauth.domain.CustomUserDetails;
import com.example.aq.user.domain.User;
import com.example.aq.user.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationProcessingFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final TokenBlacklistService tokenBlacklistService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String requestURI = request.getRequestURI();
        
        // 인증이 필요 없는 경로는 필터를 건너뜀
        if (requestURI.startsWith("/auth/") || 
            requestURI.startsWith("/swagger-ui/") || 
            requestURI.equals("/swagger-ui.html") ||
            requestURI.startsWith("/v3/api-docs") ||
            requestURI.startsWith("/swagger-resources/") ||
            requestURI.startsWith("/webjars/")) {
            filterChain.doFilter(request, response);
            return;
        }

        String refreshToken = jwtService.extractRefreshToken(request)
                .filter(jwtService::isTokenValid)
                .orElse(null);

        if (refreshToken != null) {
            checkRefreshTokenAndReIssueAccessToken(response, refreshToken);
            return;
        }

        checkAccessTokenAndAuthentication(request, response, filterChain);
    }

    private void checkRefreshTokenAndReIssueAccessToken(HttpServletResponse response, String refreshToken) {
        userRepository.findByRefreshToken(refreshToken)
                .ifPresent(user -> {
                    String reIssuedRefreshToken = reIssueRefreshToken(user);
                    String accessToken = jwtService.createAccessToken(user.getEmail(), user.getId());
                    jwtService.sendAccessAndRefreshToken(response, accessToken, reIssuedRefreshToken);
                });
    }

    private String reIssueRefreshToken(User user) {
        String reIssuedRefreshToken = jwtService.createRefreshToken();
        user.updateRefreshToken(reIssuedRefreshToken);
        userRepository.saveAndFlush(user);
        return reIssuedRefreshToken;
    }

    private void checkAccessTokenAndAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String accessToken = jwtService.extractAccessToken(request)
                .filter(token -> !tokenBlacklistService.isBlacklisted(token))
                .filter(jwtService::isTokenValid)
                .orElse(null);

        if (accessToken != null) {
            jwtService.extractEmail(accessToken)
                    .ifPresent(email -> userRepository.findByEmail(email)
                            .ifPresent(user -> {
                                CustomUserDetails userDetails = new CustomUserDetails(
                                        Collections.singleton(new SimpleGrantedAuthority(user.getRoleType().toString())),
                                        user.getEmail(),
                                        user.getRoleType(),
                                        user.getId()
                                );
                                Authentication authToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                                SecurityContextHolder.getContext().setAuthentication(authToken);
                            }));
        }

        filterChain.doFilter(request, response);
    }
}
