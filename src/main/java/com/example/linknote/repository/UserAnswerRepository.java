package com.example.linknote.repository;

import com.example.linknote.entity.UserAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RestController;

@Repository
public interface UserAnswerRepository extends JpaRepository<UserAnswer, Long> {
    UserAnswer save(UserAnswer answer);
}
