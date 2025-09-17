package com.example.aq.global.security;

import com.example.aq.domain.user.entity.User;
import com.example.aq.domain.user.repository.UserRepository;
import com.example.aq.global.jwt.JwtService;
import com.example.aq.global.oauth.domain.CustomUserDetails;
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

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationProcessingFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        
        // /auth/login 경로는 JWT 검증을 건너뜀
        String requestURI = request.getRequestURI();
        if (requestURI.equals("/auth/login") || requestURI.startsWith("/swagger-ui") || requestURI.startsWith("/v3/api-docs")) {
            filterChain.doFilter(request, response);
            return;
        }
        
        String authorizationHeader = request.getHeader("Authorization");
        
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String token = authorizationHeader.substring(7);
            
            if (jwtService.isTokenValid(token)) {
                jwtService.extractUserId(token).ifPresent(userId -> {
                    userRepository.findById(userId).ifPresent(user -> {
                        if (user.isActive()) {
                            setAuthentication(user);
                        }
                    });
                });
            }
        }
        
        filterChain.doFilter(request, response);
    }

    private void setAuthentication(User user) {
        CustomUserDetails userDetails = new CustomUserDetails(
                Collections.singleton(new SimpleGrantedAuthority("ROLE_" + user.getRoleType().toString())),
                user.getEmail(),
                user.getRoleType(),
                user.getId()
        );

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
