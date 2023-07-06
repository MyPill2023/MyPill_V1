package com.mypill.domain.question.service;

import com.mypill.domain.question.entity.NutrientQuestion;
import com.mypill.domain.question.repository.NutrientQuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NutrientQuestionService {
    private final NutrientQuestionRepository nutrientQuestionRepository;

    public List<NutrientQuestion> findByQuestionId(Long questionId){
        return nutrientQuestionRepository.findByQuestionId(questionId);
    }
}
