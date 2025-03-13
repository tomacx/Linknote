package com.example.linknote.repository;

import com.example.linknote.entity.Question;
import com.example.linknote.entity.User;
import com.example.linknote.entity.WrongAnswer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface WrongAnswerRepository extends JpaRepository<WrongAnswer, Long> {

    // 根据用户查询所有错题（按时间倒序）
    List<WrongAnswer> findByUserOrderByCreatedAtDesc(User user);

    // 分页查询用户错题
    Page<WrongAnswer> findByUser(User user, Pageable pageable);

    // 查询指定时间范围内的错题
    List<WrongAnswer> findByUserAndCreatedAtBetween(User user, LocalDateTime start, LocalDateTime end);

    // 检查是否已存在相同错误记录
    boolean existsByUserAndQuestionAndWrongAnswer(User user, Question question, String wrongAnswer);

    // 根据问题查询对应的错题记录
    List<WrongAnswer> findByQuestion(Question question);

    void deleteByQuestionId(Long questionId);
}