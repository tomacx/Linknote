package com.example.linknote.controller;

import com.example.linknote.entity.PdfDocument;
import com.example.linknote.entity.User;
import com.example.linknote.repository.PdfDocumentRepository;
import com.example.linknote.repository.UserRepository;
import com.example.linknote.service.AIClassificationService;
import com.example.linknote.service.FileStorageService;
import com.example.linknote.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/files")
public class FileController {

    private final FileStorageService storageService;
    private final AIClassificationService aiService;
    private final UserService userService;
    private final UserRepository userRepository;
    private final PdfDocumentRepository pdfDocumentRepository;
    private final FileStorageService fileStorageService;

    public FileController(FileStorageService storageService,
                          AIClassificationService aiService, UserService userService,
                          UserRepository userRepository, PdfDocumentRepository pdfDocumentRepository, FileStorageService fileStorageService) {
        this.storageService = storageService;
        this.aiService = aiService;
        this.userService = userService;
        this.userRepository = userRepository;
        this.pdfDocumentRepository = pdfDocumentRepository;
        this.fileStorageService = fileStorageService;
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
            response.put("filePath", filePath+"/"+user.getUsername());
            return ResponseEntity.accepted().body(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", e.getMessage()));
        }
    }
    @GetMapping("/{userId}")
    public ResponseEntity<?> getUserFiles(@PathVariable("userId") Long userId) {
        List<PdfDocument> files = userService.getUserUploadedFiles(userId);
        if (files.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("用户未上传文件");
        }
        return ResponseEntity.ok(files); // Spring 会自动转为 JSON
    }

    // 文件
    @DeleteMapping("delete/{fileId}")
    public ResponseEntity<?> deleteFile(@PathVariable Long fileId) {
        try {
            fileStorageService.deleteFileAndRelatedData(fileId);
            return ResponseEntity.ok("文件及相关数据删除成功");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("删除失败: " + e.getMessage());
        }
    }
}
