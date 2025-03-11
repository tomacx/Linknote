package com.example.linknote.controller;

import com.example.linknote.entity.User;
import com.example.linknote.entity.WrongAnswer;
import com.example.linknote.repository.UserRepository;
import com.example.linknote.service.UserService;
import com.example.linknote.service.WrongAnswerService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import com.example.linknote.entity.User;

import java.util.List;

@RestController
@RequestMapping("/api/wrong-answers")
@RequiredArgsConstructor
public class WrongAnswerController {
    private final WrongAnswerService wrongAnswerService;
    private final UserRepository userRepository;

    // 获取当前用户的错题列表
    @GetMapping
    public ResponseEntity<List<WrongAnswer>> getMyWrongAnswers(
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(wrongAnswerService.getUserWrongAnswers(user));
    }

    // 分页查询
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
}