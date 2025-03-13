package com.example.linknote.repository;

import com.example.linknote.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {
    Question save(Question question);

    Question findById(long id);

    @Query("SELECT q FROM Question q WHERE q.user.id = :userId AND NOT EXISTS " +
            "(SELECT ua FROM UserAnswer ua WHERE ua.user.id = :userId AND ua.question.id = q.id)")
    List<Question> findUnansweredByUserId(@Param("userId") Long userId);

    void deleteByDocumentId(Long documentId);

    List<Question> findByDocumentId(Long documentId);
}