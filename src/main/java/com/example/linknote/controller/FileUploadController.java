package com.example.linknote.controller;

import com.example.linknote.entity.User;
import com.example.linknote.repository.UserRepository;
import com.example.linknote.service.AIClassificationService;
import com.example.linknote.service.FileStorageService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/files")
public class FileUploadController {

    private final FileStorageService storageService;
    private final AIClassificationService aiService;

    private final UserRepository userRepository;

    public FileUploadController(FileStorageService storageService,
                                AIClassificationService aiService,
                                UserRepository userRepository) {
        this.storageService = storageService;
        this.aiService = aiService;
        this.userRepository = userRepository;
    }

    @PostMapping("/upload")
    public ResponseEntity<Map<String, String>> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam("userId") Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        try {
            String filePath = storageService.storeFile(file);
            aiService.processFileAsync(file, filePath, user);

            Map<String, String> response = new HashMap<>();
            response.put("message", "文件上传成功，处理中");
            response.put("filePath", filePath);
            return ResponseEntity.accepted().body(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", e.getMessage()));
        }
    }
}
