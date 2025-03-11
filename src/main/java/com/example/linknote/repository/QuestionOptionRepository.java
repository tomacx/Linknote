package com.example.linknote.repository;

import com.example.linknote.entity.QuestionOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionOptionRepository extends JpaRepository<QuestionOption, Long> {

    // 根据问题ID获取所有选项（按顺序排序）
    List<QuestionOption> findByQuestionIdOrderByOrderAsc(Long questionId);

    // 批量删除选项
    @Modifying
    @Query("DELETE FROM QuestionOption o WHERE o.question.id = :questionId")
    void deleteByQuestionId(Long questionId);

    // 统计某个问题的选项数量
    Long countByQuestionId(Long questionId);
}