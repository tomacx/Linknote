package com.example.linknote.controller;

import com.example.linknote.dto.LoginRequest;
import com.example.linknote.dto.UserRegistrationDto;
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
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        // Spring Security处理实际登录逻辑
        return ResponseEntity.ok("登录成功");
    }
}