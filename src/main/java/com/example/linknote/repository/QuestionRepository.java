package com.example.linknote.repository;

import com.example.linknote.entity.Question;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {
    Question save(Question question);

    Question findById(long id);
}
