package com.example.linknote.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "question_options")
@Getter
@Setter
public class QuestionOption {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "option_value", nullable = false)
    private String value;

    @Column(name = "option_order") // 选项顺序
    private Integer order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;

    // 可选：是否为正确答案（适用于多选题场景）
    private Boolean isCorrect = false;
}