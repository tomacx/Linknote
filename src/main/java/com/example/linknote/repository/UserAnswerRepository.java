package com.example.linknote.repository;

import com.example.linknote.entity.Question;
import com.example.linknote.entity.UserAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Repository
public interface UserAnswerRepository extends JpaRepository<UserAnswer, Long> {
    UserAnswer save(UserAnswer answer);

    // 检查用户是否已回答过某个问题
    boolean existsByUserIdAndQuestionId(Long userId, Long questionId);

    // 自定义查询：获取用户未回答的问题（通过排除已答问题）
    @Query("SELECT q FROM Question q WHERE q.user.id = :userId AND NOT EXISTS " +
            "(SELECT ua FROM UserAnswer ua WHERE ua.user.id = :userId AND ua.question.id = q.id)")
    List<Question> findUnansweredQuestionsByUserId(@Param("userId") Long userId);

    void deleteByQuestionId(Long question_id);
}
