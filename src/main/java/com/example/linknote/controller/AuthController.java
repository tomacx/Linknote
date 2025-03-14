package com.example.linknote.controller;

import com.example.linknote.dto.LoginRequest;
import com.example.linknote.dto.UserRegistrationDto;
import com.example.linknote.entity.User;
import com.example.linknote.repository.UserRepository;
import com.example.linknote.service.UserService;
import lombok.RequiredArgsConstructor;
import org.hibernate.grammars.hql.HqlParser;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;
    private final UserRepository userRepository;

    @PostMapping(value = "/register",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> register(@RequestBody UserRegistrationDto dto) {
        try {
            userService.registerUser(dto);
            User user = userRepository.findByUsername(dto.getUsername());
            System.out.println(user);
            return ResponseEntity.ok(user);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // TODO：登陆时间修改
    @PostMapping(value = "/login",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> login(@RequestBody LoginRequest login) {
        // 登陆判断linknote
        if (userService.loginUser(login)) {
            User user = userRepository.getUserByUsername(login.getUsername());
            LocalDateTime date = LocalDateTime.now();//获得系统时间.
//            String nowTime = new SimpleDateFormat("yyyy-MM-ddHH:mm:ss").format(date);//将时间格式转换成符合Timestamp要求的格式.
//            Timestamp goodsC_date =Timestamp.valueOf(nowTime);
            user.setLastLogin(date);
            userRepository.save(user);
            return ResponseEntity.ok(user); // 直接返回 User 对象，Spring 自动转为 JSON
        }
        return ResponseEntity.badRequest().body("登录失败");
    }

    // TODO:成就表


    // TODO:任务表



}