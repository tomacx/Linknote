package com.example.linknote.service;

import com.example.linknote.dto.NoteCreateDTO;
import com.example.linknote.entity.Note;
import com.example.linknote.entity.User;

import com.example.linknote.repository.NoteRepository;
import com.example.linknote.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class NoteService {
    @Value("${deepseekr1.api.url}")
    private String apiUrl;

    @Value("${deepseekr1.api.key}")
    private String apiKey;

    private final NoteRepository noteRepository;
    private final UserRepository userRepository;
    private final RestTemplate restTemplate;

    public NoteService(NoteRepository noteRepository, UserRepository userRepository,RestTemplate restTemplate) {
        this.noteRepository = noteRepository;
        this.userRepository = userRepository;
        this.restTemplate = restTemplate;
    }

    public Note createNote(NoteCreateDTO dto) {
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        Note note = new Note();
        if(dto.getTitle().length() > 200){
            throw new RuntimeException("标题不能超过200字");
        }
        note.setTitle(dto.getTitle());
        note.setContent(dto.getContent());
        note.setUser(user);
        note.setCategory(getCategory(dto.getContent()));

        return noteRepository.save(note);
    }

    private String getCategory(String text) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        String prompt = "请分析以下文本内容，返回最相关的分类（单个分类名称）：\n" +
                truncateText(text, 3000);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "deepseek-chat");
        requestBody.put("messages", List.of(Map.of(
                "role", "user",
                "content", prompt
        )));
        requestBody.put("temperature", 0.7);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(apiUrl, entity, Map.class);
            return parseResponse(response.getBody());
        } catch (Exception e) {
            return "分类失败";
        }
    }

    private String parseResponse(Map<String, Object> response) {
        try {
            List<Map<String, Object>> choices = (List<Map<String, Object>>) response.get("choices");
            Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
            return ((String) message.get("content")).replaceAll("\\s+", "");
        } catch (Exception e) {
            return "解析失败";
        }
    }
    private String truncateText(String text, int maxLength) {
        return text.length() > maxLength ? text.substring(0, maxLength) : text;
    }
}
