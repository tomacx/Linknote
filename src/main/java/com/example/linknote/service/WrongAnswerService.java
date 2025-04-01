package com.example.linknote.service;

import com.example.linknote.entity.*;
import com.example.linknote.repository.PdfDocumentRepository;
import com.example.linknote.repository.UserRepository;
import com.example.linknote.repository.WrongAnswerRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import javax.swing.text.Document;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class WrongAnswerService {
    private final UserRepository userRepository;
    @Value("${deepseekr1.api.key}")
    private String apiKey;

    private static final String API_URL = "https://api.deepseek.com/chat/completions";

    private final WrongAnswerRepository wrongAnswerRepository;
    private final QuestionService questionService;
    private final PdfDocumentRepository pdfDocumentRepository;

    // 保存错题记录
    @Transactional
    public WrongAnswer createWrongAnswer(User user, Long questionId, String wrongAnswer) {
        Question question = questionService.getQuestionById(questionId);

        WrongAnswer record = new WrongAnswer();
        record.setUser(user);
        record.setQuestion(question);
        record.setWrongAnswer(wrongAnswer);
        record.setCreatedAt(LocalDateTime.now());
        PdfDocument pdfDocument = pdfDocumentRepository.getPdfDocumentById(question.getPdfDocumentId());
        record.setPdfDocument1(pdfDocument);
        record.setCategory(pdfDocument.getCategory());

        return wrongAnswerRepository.save(record);
    }

    // 获取用户所有错题
    public List<WrongAnswer> getUserWrongAnswers(User user) {
        return wrongAnswerRepository.findByUserOrderByCreatedAtDesc(user);
    }

    // 分页获取错题
    public Page<WrongAnswer> getWrongAnswersByPage(User user, Pageable pageable) {
        return wrongAnswerRepository.findByUser(user, pageable);
    }

    // 获取近期错题（最近7天）
    public List<WrongAnswer> getRecentWrongAnswers(User user) {
        return wrongAnswerRepository.findByUserAndCreatedAtBetween(
                user,
                LocalDateTime.now().minusDays(7),
                LocalDateTime.now()
        );
    }

    // 获取指定问题的所有错答记录
    public List<WrongAnswer> getWrongAnswersByQuestion(Long questionId) {
        Question question = questionService.getQuestionById(questionId);
        return wrongAnswerRepository.findByQuestion(question);
    }

    // 删除单个错题记录
    @Transactional
    public void deleteWrongAnswer(Long recordId) {
        wrongAnswerRepository.deleteById(recordId);
    }

    // 批量删除错题
    @Transactional
    public void deleteBatchWrongAnswers(List<Long> recordIds) {
        wrongAnswerRepository.deleteAllById(recordIds);
    }

    //
    public String analyse(long userId) throws JsonProcessingException {
        RestTemplate restTemplate = new RestTemplate();
        User user = userRepository.findById(userId).orElse(null);

        List<WrongAnswer> wrongAnswers = wrongAnswerRepository.getWrongAnswersByUser(user);
        String content = "";
        for(WrongAnswer wrongAnswer : wrongAnswers) {
            Question question = wrongAnswer.getQuestion();
            content += question.getContent();
            content += "\n";
        }
        String prompt = String.format("现在用户需要进行错题分析，下面的内容是用户错掉的题目，请根据这些错题，对用户的学习情况进行分析"
                        + "按JSON格式返回，分析的内容用content字段存，错题内容如下：\n\n%s", content);

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(apiKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "deepseek-chat");
        requestBody.put("messages", List.of(Map.of("role", "user", "content", prompt)));
        requestBody.put("temperature", 0.7);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(API_URL, entity, String.class);

        // 返回Json格式内容
        String  jsonResponse = response.getBody();

        System.out.println(jsonResponse);

        // 进行Json格式内容解析
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(jsonResponse);

        JsonNode contentNode = root.path("choices").get(0).path("message").path("content");
        String contentJson = contentNode.asText();

        // 清理 Markdown 的代码块标记
        String cleanedJson = contentJson
                .replaceAll("```json", "")
                .replaceAll("```", "")
                .trim();

        JsonNode questionsNode = mapper.readTree(cleanedJson);
        System.out.println(cleanedJson);
        String res = questionsNode.path("content").asText();
        return res;
    }
}