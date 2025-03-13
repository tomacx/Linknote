package com.example.linknote.service;

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
public class AIGenerateService {
    @Value("${deepseekr1.api.key}")
    private String apiKey;

    private static final String API_URL = "https://api.deepseek.com/chat/completions";

    public String generateQuestions(String content, int count) {
        RestTemplate restTemplate = new RestTemplate();

        String prompt = String.format("请根据以下内容分别生成%d道难度为简单、中等、困难的题目，要求包含选择题、填空题和简答题，选择题将题目和选项分开，然后题型写在type字段里，不要另外写"
                        + "按JSON格式返回，包含content、answer、type、difficulty字段，选择题另外增加options字段：\n\n%s",
                count, content);

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(apiKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "deepseek-chat");
        requestBody.put("messages", List.of(Map.of("role", "user", "content", prompt)));
        requestBody.put("temperature", 0.7);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(API_URL, entity, String.class);
        return response.getBody();
    }
}