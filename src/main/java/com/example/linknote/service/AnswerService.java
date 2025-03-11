package com.example.linknote.service;

import com.example.linknote.entity.Question;
import com.example.linknote.entity.User;
import com.example.linknote.entity.UserAnswer;
import com.example.linknote.repository.QuestionRepository;
import com.example.linknote.repository.UserAnswerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AnswerService {
    @Autowired
    private  UserAnswerRepository userAnswerRepository;
    @Autowired
    private  QuestionRepository questionRepository;
    @Autowired
    private WrongAnswerService wrongAnswerService;
    public UserAnswer submitAnswer(long questionId, String userAnswer, User user) {
        Question question = questionRepository.findById(questionId);

        UserAnswer answer = new UserAnswer();
        answer.setQuestion(question);
        answer.setUser(user);
        answer.setUserAnswer(userAnswer);
        answer.setCorrect(checkAnswer(question, userAnswer));
        // 如果回答错误，记录到错题本
        if (!answer.isCorrect()) {
            wrongAnswerService.createWrongAnswer(user, questionId, userAnswer);
        }
        return userAnswerRepository.save(answer);
    }

    private boolean checkAnswer(Question question, String userAnswer) {
        // 根据题目类型实现判题逻辑
        return question.getAnswer().equalsIgnoreCase(userAnswer.trim());
    }
}