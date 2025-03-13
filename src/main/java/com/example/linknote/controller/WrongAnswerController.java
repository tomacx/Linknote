package com.example.linknote.controller;

import com.example.linknote.entity.User;
import com.example.linknote.entity.WrongAnswer;
import com.example.linknote.repository.UserRepository;
import com.example.linknote.repository.WrongAnswerRepository;
import com.example.linknote.service.UserService;
import com.example.linknote.service.WrongAnswerService;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import com.example.linknote.entity.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/wrong-answers")
@RequiredArgsConstructor
public class WrongAnswerController {
    private final WrongAnswerService wrongAnswerService;
    private final UserRepository userRepository;
    private final WrongAnswerRepository wrongAnswerRepository;

    // 获取当前用户的错题列表
    @GetMapping
    public ResponseEntity<List<WrongAnswer>> getMyWrongAnswers(
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(wrongAnswerService.getUserWrongAnswers(user));
    }

    // 分页查询  TODO：给前端错题的文件来源和分类
    @GetMapping("/page/{userid}")
    public ResponseEntity<Page<WrongAnswer>> getWrongAnswersByPage(
            @PathVariable Long userid,
            Pageable pageable) {
        User user = userRepository.findById(userid).orElse(null);
        return ResponseEntity.ok(wrongAnswerService.getWrongAnswersByPage(user, pageable));
    }

    // 删除错题记录
    @DeleteMapping("delete/{id}")
    public ResponseEntity<Void> deleteWrongAnswer(@PathVariable Long id) {
        wrongAnswerService.deleteWrongAnswer(id);
        return ResponseEntity.noContent().build();
    }

    // 获取指定问题的错答记录
    @GetMapping("/question/{questionId}")
    public ResponseEntity<List<WrongAnswer>> getQuestionWrongAnswers(
            @PathVariable Long questionId) {
        return ResponseEntity.ok(wrongAnswerService.getWrongAnswersByQuestion(questionId));
    }

    // TODO：错题分析  finished
    @GetMapping("/wrong/analyse/{userId}")
    public ResponseEntity<Map<String, Object>> getWrongAnswersByAnalyse(@PathVariable Long userId) throws JsonProcessingException {
        Map<String, Object> response = new HashMap<>();
        response.put("userId", userId);
        response.put("analysis", wrongAnswerService.analyse(userId));
        return ResponseEntity.ok(response);
    }

    // 根据类别进行返回 TODO
    @GetMapping("/wrong/{category}")
    public ResponseEntity<List<WrongAnswer>> getWrongAnswersByCategory(@PathVariable String category) {
        return ResponseEntity.ok(wrongAnswerRepository.findByCategory(category));
    }


}