package com.catchcbnu.ospp_project.user.service;

import com.catchcbnu.ospp_project.user.entity.User;
import com.catchcbnu.ospp_project.user.dto.UserResponse;
import com.catchcbnu.ospp_project.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public UserResponse getUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        return toResponse(user);
    }

    private UserResponse toResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getNickname(),
                user.getCollege(),
                user.getDepartment(),
                user.getLevel(),
                user.getExp()
        );
    }
}
