package com.example.linknote.service;

import com.example.linknote.entity.*;
import com.example.linknote.repository.WrongAnswerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class WrongAnswerService {

    private final WrongAnswerRepository wrongAnswerRepository;
    private final QuestionService questionService;

    // 保存错题记录
    @Transactional
    public WrongAnswer createWrongAnswer(User user, Long questionId, String wrongAnswer) {
        Question question = questionService.getQuestionById(questionId);

        WrongAnswer record = new WrongAnswer();
        record.setUser(user);
        record.setQuestion(question);
        record.setWrongAnswer(wrongAnswer);
        record.setCreatedAt(LocalDateTime.now());

        return wrongAnswerRepository.save(record);
    }

    // 获取用户所有错题
    public List<WrongAnswer> getUserWrongAnswers(User user) {
        return wrongAnswerRepository.findByUserOrderByCreatedAtDesc(user);
    }

    // 分页获取错题
    public Page<WrongAnswer> getWrongAnswersByPage(User user, Pageable pageable) {
        return wrongAnswerRepository.findByUser(user, pageable);
    }

    // 获取近期错题（最近7天）
    public List<WrongAnswer> getRecentWrongAnswers(User user) {
        return wrongAnswerRepository.findByUserAndCreatedAtBetween(
                user,
                LocalDateTime.now().minusDays(7),
                LocalDateTime.now()
        );
    }

    // 获取指定问题的所有错答记录
    public List<WrongAnswer> getWrongAnswersByQuestion(Long questionId) {
        Question question = questionService.getQuestionById(questionId);
        return wrongAnswerRepository.findByQuestion(question);
    }

    // 删除单个错题记录
    @Transactional
    public void deleteWrongAnswer(Long recordId) {
        wrongAnswerRepository.deleteById(recordId);
    }

    // 批量删除错题
    @Transactional
    public void deleteBatchWrongAnswers(List<Long> recordIds) {
        wrongAnswerRepository.deleteAllById(recordIds);
    }
}