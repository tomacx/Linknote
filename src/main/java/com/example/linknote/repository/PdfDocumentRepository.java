package com.example.linknote.repository;

import com.example.linknote.entity.PdfDocument;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PdfDocumentRepository extends JpaRepository<PdfDocument, Long> {
}