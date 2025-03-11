package com.example.linknote.service;

import com.example.linknote.entity.QuestionOption;
import com.example.linknote.repository.QuestionOptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class QuestionOptionService {
    private final QuestionOptionRepository optionRepository;

    // 保存单个选项
    public QuestionOption saveOption(QuestionOption option) {
        return optionRepository.save(option);
    }

    // 批量保存选项
    public List<QuestionOption> saveOptions(List<QuestionOption> options) {
        return optionRepository.saveAll(options);
    }

    // 获取问题的所有选项
    public List<QuestionOption> getOptionsByQuestion(Long questionId) {
        return optionRepository.findByQuestionIdOrderByOrderAsc(questionId);
    }

    // 更新选项内容
    public QuestionOption updateOption(Long optionId, String newValue) {
        QuestionOption option = optionRepository.findById(optionId)
                .orElseThrow(() -> new RuntimeException("Option not found"));
        option.setValue(newValue);
        return optionRepository.save(option);
    }

    // 删除单个选项
    public void deleteOption(Long optionId) {
        optionRepository.deleteById(optionId);
    }

    // 清空问题的所有选项
    public void clearOptions(Long questionId) {
        optionRepository.deleteByQuestionId(questionId);
    }
}