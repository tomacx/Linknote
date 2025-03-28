package com.example.linknote.controller;

import com.example.linknote.dto.NoteCreateDTO;
import com.example.linknote.entity.Note;
import com.example.linknote.entity.PdfDocument;
import com.example.linknote.entity.User;
import com.example.linknote.repository.NoteRepository;
import com.example.linknote.repository.PdfDocumentRepository;
import com.example.linknote.repository.UserRepository;
import com.example.linknote.service.AIClassificationService;
import com.example.linknote.service.FileStorageService;
import com.example.linknote.service.NoteService;
import com.example.linknote.service.UserService;
import com.fasterxml.jackson.databind.util.JSONPObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
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
    private final NoteService noteService;
    private final NoteRepository noteRepository;

    public FileController(FileStorageService storageService,
                          AIClassificationService aiService, UserService userService,
                          UserRepository userRepository,
                          PdfDocumentRepository pdfDocumentRepository,
                          FileStorageService fileStorageService,
                          NoteService noteService, NoteRepository noteRepository) {
        this.storageService = storageService;
        this.aiService = aiService;
        this.userService = userService;
        this.userRepository = userRepository;
        this.pdfDocumentRepository = pdfDocumentRepository;
        this.fileStorageService = fileStorageService;
        this.noteService = noteService;
        this.noteRepository = noteRepository;
    }
// TODO：加速分类
    @PostMapping("/upload")
    public ResponseEntity<Map<String, String>> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam("userId") Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        try {
            String filePath = storageService.storeFile(file);
            aiService.processFileAsync(file, filePath, user);

            // 调用Python脚本插入PDF到数据库
            ProcessBuilder pb = new ProcessBuilder("python3", 
                "python_back/insert.py",  // 使用相对路径
                filePath, 
                userId.toString());
            Process process = pb.start();
            int exitCode = process.waitFor();

            Map<String, String> response = new HashMap<>();
            if (exitCode == 0) {
                response.put("message", "文件上传成功，处理中，并已插入数据库");
            } else {
                response.put("message", "文件上传成功，处理中，但数据库插入失败");
            }
            return ResponseEntity.accepted().body(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", e.getMessage()));
        }
    }
    // 获取用户上传的笔记文件
    @GetMapping("/{userId}")
    public ResponseEntity<?> getUserFiles(@PathVariable("userId") Long userId) {
        System.out.println("Processing request for user: {}"+userId);
        List<PdfDocument> files = userService.getUserUploadedFiles(userId);
//        System.out.println(files);
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
    // 上传用户自己写的笔记
    @PostMapping("/note")
    public ResponseEntity<?> createNote(
        @RequestBody NoteCreateDTO dto
    ){
        Note createNote  = noteService.createNote(dto);
        return ResponseEntity.ok(createNote);
    }

    // 获取用户自己写的笔记
    @GetMapping("/notes/{userId}")
    public ResponseEntity<?> getNotes(@PathVariable("userId") Long userId) {
        List<Note> notes = noteRepository.findByUserId(userId);
        if (notes.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("用户未创建笔记");
        }
        return ResponseEntity.ok(notes); // Spring 会自动转为 JSON
    }

    // 用户删除自己写的文件
    @DeleteMapping("/delete/{noteId}")
    public ResponseEntity<?> deleteNote(@PathVariable Long noteId) {
        try {
            fileStorageService.deleteFileAndRelatedData(noteId);
            return ResponseEntity.ok("笔记删除成功");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("删除失败: " + e.getMessage());
        }
    }

    // 用户编辑自己的笔记内容

}
