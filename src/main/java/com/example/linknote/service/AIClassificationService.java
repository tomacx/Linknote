package com.example.linknote.service;


import com.example.linknote.entity.PdfDocument;
import com.example.linknote.entity.User;
import com.example.linknote.repository.PdfDocumentRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AIClassificationService {

    @Value("${deepseekr1.api.url}")
    private String apiUrl;

    @Value("${deepseekr1.api.key}")
    private String apiKey;

    private final PdfProcessingService pdfService;
    private PdfDocument pdfDocument;
    private final RestTemplate restTemplate;
    private final PdfDocumentRepository pdfDocumentRepository;

    public AIClassificationService(PdfProcessingService pdfService,
                                   PdfDocumentRepository pdfDocumentRepository) {
        this.pdfService = pdfService;
        this.pdfDocumentRepository = pdfDocumentRepository;
        this.restTemplate = new RestTemplate();
    }

    @Async
    public void processFileAsync(MultipartFile file, String filePath, User user) {
        try {
            String text = pdfService.extractTextFromPdf(file.getInputStream());
            String category = getAIClassification(text);

            PdfDocument document = new PdfDocument();
            document.setFileName(file.getOriginalFilename());
            document.setFilePath("http://82.157.18.189/uploads/"+filePath);
            document.setCategory(category);
            document.setUploadTime(LocalDateTime.now());
            document.setUser(user);

            pdfDocumentRepository.save(document);
        } catch (Exception e) {
            // 处理异常
            System.out.println(e);
        }
    }

    private String getAIClassification(String text) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        String prompt = "请分析以下文本内容，返回最相关的分类，只返回一个词语，别返回其他内容：\n" +
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



















