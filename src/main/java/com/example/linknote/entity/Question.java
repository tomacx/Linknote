package com.example.linknote.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

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
    @JsonIgnore
    private List<QuestionOption> options = new ArrayList<>();

    @Transient
    @JsonProperty("options")
    private List<String> optionValues;

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

//    // Question.java 新增字段
//    @Transient // 标记为不持久化到数据库
//    private List<String> formattedOptions;
//
    // 动态获取选项值的逻辑
    @PostLoad // JPA加载后自动执行
    private void populateOptionValues() {
        this.optionValues = this.options.stream()
                .sorted(Comparator.comparingInt(QuestionOption::getOrder))
                .map(QuestionOption::getValue)
                .collect(Collectors.toList());
    }
    // getters & setters
}
