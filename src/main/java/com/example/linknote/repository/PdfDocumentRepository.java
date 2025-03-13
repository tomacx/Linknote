package com.example.linknote.repository;

import com.example.linknote.entity.PdfDocument;
import com.example.linknote.entity.Question;
import com.example.linknote.entity.WrongAnswer;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PdfDocumentRepository extends JpaRepository<PdfDocument, Long> {
    // 根据用户ID查询所有关联的PDF文档
    List<PdfDocument> findByUserId(Long userId);
}