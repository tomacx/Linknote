package com.example.linknote.controller;

import com.example.linknote.service.DeepSeekR1Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class QuestionController {

    @Autowired
    private DeepSeekR1Service deepSeekR1Service;

    @PostMapping("/ask")
    public ResponseEntity<?> askQuestion(@RequestBody Map<String, String> payload) {
        try {
            String question = payload.get("question");
            String answer = deepSeekR1Service.askQuestion(question);
            return ResponseEntity.ok().body(Map.of("answer", answer));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("获取答案失败: " + e.getMessage());
        }
    }
}