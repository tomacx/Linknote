package com.example.linknote.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.sql.Date;
import java.time.LocalDateTime;

@Entity
@Table(name="pdf_documents")
@Getter
@Setter
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class PdfDocument {
    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="file_name",nullable = false)
    private String fileName;
    @Column(name = "file_path", nullable = false)
    private String filePath;

    private String category;



    @Column(name = "upload_time", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime uploadTime;

    @Column(name = "file_root", nullable = false)
    private String fileRoot;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PdfDocument)) return false;
        return id != null && id.equals(((PdfDocument) o).getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
