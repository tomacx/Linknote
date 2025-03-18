package com.example.linknote.controller;

import com.example.linknote.entity.Question;
import com.example.linknote.entity.QuestionOption;
import com.example.linknote.entity.User;
import com.example.linknote.entity.UserAnswer;
import com.example.linknote.repository.QuestionOptionRepository;
import com.example.linknote.repository.QuestionRepository;
import com.example.linknote.repository.UserRepository;
import com.example.linknote.service.AnswerService;
import com.example.linknote.service.QuestionOptionService;
import com.example.linknote.service.QuestionService;
import com.example.linknote.service.UserService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/questions")
@RequiredArgsConstructor
public class CreateQuestionController {
    private final QuestionService questionService;
    private final AnswerService answerService;
    private final UserRepository userRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private UserService userService;
    @Autowired
    private QuestionOptionRepository questionOptionRepository;
    @Autowired
    private QuestionOptionService questionOptionService;

    // 获取题目
    // 获取用户未回答的问题列表
    @GetMapping("/{userId}/unanswered")
    public ResponseEntity<?> getUnansweredQuestions(@PathVariable Long userId) {
        List<Question> questions = userService.getUnansweredQuestions(userId);
        return ResponseEntity.ok(questions);
    }
    // 获取选择题的选项
    @GetMapping("/options/{questionId}")
    public ResponseEntity<?> getOptions(@PathVariable Long questionId) {
        List<QuestionOption> options = questionOptionService.getOptionsByQuestion(questionId);
        return ResponseEntity.ok(options);
    }

    @PostMapping("/generate")
    public ResponseEntity<List<Question>> generateQuestions(
            @RequestBody QuestionGenerationRequest request) throws Exception {
        return ResponseEntity.ok(questionService.generateQuestions(
                request.getDocumentId(),
                request.getCount()
//                request.getDifficulty()
        ));
    }

    @PostMapping("/submit")
    public ResponseEntity<UserAnswer> submitAnswer(
            @RequestBody AnswerSubmission submission) {
        Question question = questionRepository.findById((long)submission.getQuestionId());
        User user = question.getUser();
        if (user == null) {
          System.out.println("No user");
        }
        return ResponseEntity.ok(answerService.submitAnswer(
                submission.getQuestionId(),
                submission.getAnswer(),
                user
                // 需要获取当前用户
        ));
    }

    // DTO定义
    @Data
    static class QuestionGenerationRequest {
        private Long documentId;
        private int count;
//        private String difficulty;
    }

    @Data
    static class AnswerSubmission {
        private Long questionId;
        private String answer;
    }

    // TODO： 统计数据

}