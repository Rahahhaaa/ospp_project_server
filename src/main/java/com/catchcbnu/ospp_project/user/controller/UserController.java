package com.catchcbnu.ospp_project.user.controller;

import com.catchcbnu.ospp_project.user.dto.UserResponse;
import com.catchcbnu.ospp_project.user.service.UserService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{userId}")
    public UserResponse getUser(@PathVariable Long userId) {
        return userService.getUser(userId);
    }
}
