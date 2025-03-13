package com.example.linknote.service;

import com.example.linknote.entity.*;
import com.example.linknote.repository.PdfDocumentRepository;
import com.example.linknote.repository.QuestionRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.ErrorManager;

@Service
@RequiredArgsConstructor
public class QuestionService {
    private final PdfDocumentRepository pdfDocumentRepository;
    private final QuestionRepository questionRepository;
    private final AIGenerateService aiGenerateService;

    public List<Question> generateQuestions(Long documentId, int count) throws Exception {
        PdfDocument document = pdfDocumentRepository.findById(documentId).orElseThrow();
        String fileContent = extractTextFromPdf(document.getFilePath());
//        System.out.println(fileContent);
        String aiResponse = aiGenerateService.generateQuestions(fileContent, count);
        System.out.println(aiResponse);
        return parseAndSaveQuestions(aiResponse, document);
    }

    private List<Question> parseAndSaveQuestions(String jsonResponse, PdfDocument document) throws Exception {
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
        // TODO：Need to fix the generate of question, json can't be explained rightly
        System.out.println(cleanedJson);
        List<Question> questions = new ArrayList<>();
        for (JsonNode node : questionsNode) {
            Question question = new Question();
            question.setContent(node.path("content").asText());
            question.setAnswer(node.path("answer").asText());
            question.setType(node.path("type").asText());
            if ("选择题".equals(question.getType())) {
                JsonNode optionsNode = node.path("options");
                if (optionsNode.isArray()) {
                    int order = 1;
                    for (JsonNode optNode : optionsNode) {
                        QuestionOption option = new QuestionOption();
                        option.setValue(optNode.asText());
                        option.setOrder(order++);
                        option.setQuestion(question);
                        question.getOptions().add(option);
                    }
                }
            }
            question.setDifficulty(node.path("difficulty").asText());
           // question.setDifficulty(node.path("difficulty").asText());
            question.setDocument(document);
            question.setUser(document.getUser());
            questions.add(questionRepository.save(question));
        }
        return questions;
    }

    private String extractTextFromPdf(String filePath) throws IOException {
        try (PDDocument document = PDDocument.load(new File(filePath))) {
            PDFTextStripper stripper = new PDFTextStripper();
            System.out.println("提取成功");
            return stripper.getText(document);
        } catch (Exception e) {
            throw new RuntimeException("PDF内容提取失败", e);
        }


    }
    public Question getQuestionById(long id){
        return questionRepository.findById(id);
    }
}