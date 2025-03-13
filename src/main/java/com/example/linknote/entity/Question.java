package com.example.linknote.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "questions")
@Getter
@Setter
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT")
    private String content;

    private String answer;
    private String difficulty;
    private String type; // 题目类型：单选/多选/填空等

//    @ElementCollection
//    @CollectionTable(name = "question_options", joinColumns = @JoinColumn(name = "question_id"))
//    // 修改Question.java中的options字段
    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("order ASC") // 按顺序排列
    private List<QuestionOption> options = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "document_id")
    private PdfDocument document;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public long getPdfDocumentId(
    ){
        return document.getId();
    }

    // getters & setters
}
