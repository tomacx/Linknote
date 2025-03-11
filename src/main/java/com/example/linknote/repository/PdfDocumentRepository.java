package com.example.linknote.repository;

import com.example.linknote.entity.PdfDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PdfDocumentRepository extends JpaRepository<PdfDocument, Long> {

}