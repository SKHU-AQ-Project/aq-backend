package com.example.aq.app.user.service;

import com.example.aq.common.exception.ResourceNotFoundException;
import com.example.aq.common.exception.UnauthorizedException;
import com.example.aq.app.user.domain.User;
import com.example.aq.app.user.dto.UpdateUserRequest;
import com.example.aq.app.user.dto.UserResponse;
import com.example.aq.app.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class UserService {

    private final UserRepository userRepository;

    public UserResponse getUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("사용자", "id", id));
        
        if (!user.getEnabled()) {
            throw new ResourceNotFoundException("사용자", "id", id);
        }
        
        return UserResponse.of(user);
    }

    public UserResponse getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("사용자", "email", email));
        
        if (!user.getEnabled()) {
            throw new ResourceNotFoundException("사용자", "email", email);
        }
        
        return UserResponse.of(user);
    }

    public UserResponse getUserByNickname(String nickname) {
        User user = userRepository.findByNickname(nickname)
                .orElseThrow(() -> new ResourceNotFoundException("사용자", "nickname", nickname));
        
        if (!user.getEnabled()) {
            throw new ResourceNotFoundException("사용자", "nickname", nickname);
        }
        
        return UserResponse.of(user);
    }

    @Transactional
    public UserResponse updateUser(Long id, UpdateUserRequest request, Long userId) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("사용자", "id", id));

        // 본인 확인
        if (!user.getId().equals(userId)) {
            throw new UnauthorizedException("사용자 정보를 수정할 권한이 없습니다");
        }

        // 닉네임 중복 확인 (변경하는 경우에만)
        if (request.getNickname() != null && !request.getNickname().equals(user.getNickname())) {
            if (userRepository.existsByNickname(request.getNickname())) {
                throw new IllegalArgumentException("이미 사용 중인 닉네임입니다");
            }
        }

        // 사용자 정보 업데이트
        user.updateProfile(
                request.getNickname(),
                request.getBio(),
                request.getProfileImageUrl(),
                request.getInterests()
        );

        User savedUser = userRepository.save(user);

        log.info("사용자 정보가 수정되었습니다: {}", savedUser.getId());
        return UserResponse.of(savedUser);
    }

    @Transactional
    public UserResponse addPoints(Long id, Integer points) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("사용자", "id", id));
        
        user.addPoints(points);
        User savedUser = userRepository.save(user);
        
        log.info("사용자 포인트가 추가되었습니다: {} (+{})", savedUser.getId(), points);
        return UserResponse.of(savedUser);
    }
}

