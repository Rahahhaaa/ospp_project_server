package com.catchcbnu.ospp_project.auth.service;

import com.catchcbnu.ospp_project.auth.dto.SignupRequest;
import com.catchcbnu.ospp_project.auth.dto.SignupResponse;
import com.catchcbnu.ospp_project.common.exception.DuplicateEmailException;
import com.catchcbnu.ospp_project.common.exception.DuplicateNicknameException;
import com.catchcbnu.ospp_project.user.entity.User;
import com.catchcbnu.ospp_project.user.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public SignupResponse signup(SignupRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new DuplicateEmailException();
        }
        if (userRepository.existsByNickname(request.nickname())) {
            throw new DuplicateNicknameException();
        }

        String passwordHash = passwordEncoder.encode(request.password());
        User user = new User(request.email(), passwordHash, request.nickname(), request.college(), request.department());
        User saved = userRepository.save(user);

        return new SignupResponse(saved.getId());
    }
}
