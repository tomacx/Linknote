package com.example.linknote.dto;

import lombok.Data;

@Data
public class NoteCreateDTO {
    private String title;
    private String content;
    private Long userId;
}
