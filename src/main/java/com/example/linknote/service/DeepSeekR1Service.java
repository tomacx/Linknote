package com.example.linknote.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;

@Service
public class DeepSeekR1Service {
    // use to send message
    @Value("${deepseekr1.api.url}")
    private String apiUrl;

    @Value("${deepseekr1.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate;

    public DeepSeekR1Service(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }
    // make the information to send
    public String askQuestion(String question) {
        // 创建请求头
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + apiKey);
        headers.set("Content-Type", "application/json");

        // 创建请求体
        String requestBody = createRequestBody(question);

        // 创建HttpEntity
        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

        // 发送请求
        ResponseEntity<String> response = restTemplate.exchange(
                apiUrl, HttpMethod.POST, entity, String.class);

        // 返回响应体
        return response.getBody();
    }
    // create the true body to ask question
    private String createRequestBody(String question) {
        // 构建请求体，指定模型名称
        return "{ \"model\": \"deepseek-chat\", \"messages\": [{ \"role\": \"user\", \"content\": \"" + question + "\" }] }";
    }
}