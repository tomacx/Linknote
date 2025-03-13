package com.example.linknote.controller;

import com.example.linknote.dto.LoginRequest;
import com.example.linknote.dto.UserRegistrationDto;
import com.example.linknote.entity.User;
import com.example.linknote.repository.UserRepository;
import com.example.linknote.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
            return new ResponseEntity<>("注册成功", HttpStatus.OK);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest login) {
        // 登陆判断
        if (userService.loginUser(login)) {
            User user = userService.getUserByUsername(login.getUsername());
            return ResponseEntity.ok(user); // 直接返回 User 对象，Spring 自动转为 JSON
        }
        return ResponseEntity.badRequest().body("登录失败");
    }
}