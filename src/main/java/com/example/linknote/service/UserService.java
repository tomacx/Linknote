package com.example.linknote.service;

import com.example.linknote.dto.LoginRequest;
import com.example.linknote.dto.UserRegistrationDto;
import com.example.linknote.entity.PdfDocument;
import com.example.linknote.entity.Question;
import com.example.linknote.entity.User;
import com.example.linknote.repository.PdfDocumentRepository;
import com.example.linknote.repository.QuestionRepository;
import com.example.linknote.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final PdfDocumentRepository pdfDocumentRepository;
    private final QuestionRepository questionRepository;
    @Autowired
    private FileStorageService fileStorageService;
    public User registerUser(UserRegistrationDto registrationDto) {
        if (userRepository.existsByUsername(registrationDto.getUsername())) {
            throw new RuntimeException("用户名已存在");
        }
        if (userRepository.existsByEmail(registrationDto.getEmail())) {
            throw new RuntimeException("邮箱已被注册");
        }

        User user = new User();
        user.setUsername(registrationDto.getUsername());
        user.setEmail(registrationDto.getEmail());
        user.setPassword(registrationDto.getPassword());
        user.setAvatarIndex(registrationDto.getAvatarIndex());

        return userRepository.save(user);
    }

    public boolean loginUser(LoginRequest loginRequest) {
        if (userRepository.existsByUsername(loginRequest.getUsername())) {
            User user = userRepository.findByUsername(loginRequest.getUsername());
            if (loginRequest.getPassword().equals(user.getPassword())) {
                return true;
            }
        }
        return false;
    }

    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }
    // 获取用户上传的所有PDF文档
    public List<PdfDocument> getUserUploadedFiles(Long userId) {
        return pdfDocumentRepository.findByUserId(userId);
    }

    // 获取用户未回答的问题列表
    public List<Question> getUnansweredQuestions(Long userId) {
        return questionRepository.findUnansweredByUserId(userId);
    }

}