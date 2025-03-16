package com.example.linknote.service;


import com.example.linknote.entity.Question;
import com.example.linknote.entity.User;
import com.example.linknote.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;


@Service
public class FileStorageService {

    private final Path fileStorageLocation;

    public FileStorageService(@Value("${file.upload-dir}") String uploadDir) throws IOException {
        this.fileStorageLocation =  Paths.get(uploadDir).toAbsolutePath().normalize();
        Files.createDirectories(this.fileStorageLocation);
    }

        public String storeFile(MultipartFile file) throws IOException {
            String fileName = StringUtils.cleanPath(file.getOriginalFilename());
            String uniqueFileName = UUID.randomUUID() + "_" + fileName;

            Path targetLocation = this.fileStorageLocation.resolve(uniqueFileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            // 返回完整存储路径
            return uniqueFileName;
        }
    @Autowired
    private PdfDocumentRepository fileRepository;
    @Autowired
    private QuestionRepository questionRepository;
    @Autowired
    private WrongAnswerRepository wrongQuestionRepository;
    @Autowired
    private QuestionOptionRepository questionOptionRepository;
    @Autowired
    private UserAnswerRepository userAnswerRepository;

    @Transactional
    public void deleteFileAndRelatedData(Long document_Id) {
        // 获取文件对应的问题
        List<Question> questions = questionRepository.findByDocumentId(document_Id);

        for (Question question : questions) {
            Long questionId = question.getId();

            // 删除错题
            wrongQuestionRepository.deleteByQuestionId(questionId);

            // 删除选项
            questionOptionRepository.deleteById(questionId);

            userAnswerRepository.deleteByQuestionId(questionId);
        }

        // 删除问题
        questionRepository.deleteByDocumentId(document_Id);

        // 删除文件
        fileRepository.deleteById(document_Id);

    }
    }
