package com.example.linknote.entity;

import com.example.linknote.repository.PdfDocumentRepository;
import jakarta.persistence.Entity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "wrong_answers")
@Getter
@Setter
public class WrongAnswer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "question_id")
    private Question question;

    private String wrongAnswer;
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name="document_id")
    private PdfDocument pdfDocument1;

    private String category;
}

// 对应的Repository和Service需要添加查询方法