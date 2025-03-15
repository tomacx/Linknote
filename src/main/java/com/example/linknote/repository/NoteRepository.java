package com.example.linknote.repository;

import com.example.linknote.entity.Note;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NoteRepository extends JpaRepository<Note, Long> {
    // 根据用户ID查询所有关联的PDF文档
    List<Note> findByUserId(Long userId);
    Note save(Note note);
}
