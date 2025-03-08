package com.example.linknote.controller;

import com.example.linknote.dto.LoginRequest;
import com.example.linknote.dto.UserRegistrationDto;
import com.example.linknote.entity.User;
import com.example.linknote.repository.UserRepository;
import com.example.linknote.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody UserRegistrationDto dto) {
        try {
            userService.registerUser(dto);
            return ResponseEntity.ok("注册成功");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest login) {
        // 登陆判断
        if(userService.loginUser(login)) {
            User user = userService.getUserByUsername(login.getUsername());
            return ResponseEntity.ok(user.getId());
        }
        return ResponseEntity.badRequest().body("登录失败");
    }
}