package com.mypill.domain.survey.service;

import com.mypill.domain.nutrient.entity.Nutrient;
import com.mypill.domain.question.entity.NutrientQuestion;
import com.mypill.domain.question.service.NutrientQuestionService;
import com.mypill.global.AppConfig;
import com.mypill.global.rsdata.RsData;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SurveyService {
    private final NutrientQuestionService nutrientQuestionService;

    public RsData<String> validStartSurvey(Long[] categoryItemIds) {
        if (categoryItemIds.length > AppConfig.getStartMaxLength()) {
            return RsData.of("F-1", "3개 이하로 선택해주세요.");
        }
        if (categoryItemIds.length < AppConfig.getStartMinLength()) {
            return RsData.of("F-2", "1개 이상으로 선택해주세요.");
        }
        return RsData.of("S-1", "설문 완료");
    }

    public RsData<String> validCompleteSurvey(Long[] questionIds) {
        if (questionIds.length > AppConfig.getCompleteMaxLength()) {
            return RsData.of("F-1", "9개 이하로 선택해주세요.");
        }
        if (questionIds.length < AppConfig.getCompleteMinLength()) {
            return RsData.of("F-2", "1개 이상으로 선택해주세요.");
        }
        return RsData.of("S-1", "질문 완료");
    }

    public Map<String, List<Nutrient>> getAnswers (Long[] questionIds) {
        Map<String, List<Nutrient>> answers = new HashMap<>();
        for (Long id : questionIds) {
            List<NutrientQuestion> nutrientQuestions = nutrientQuestionService.findByQuestionId(id);
            Map<String, List<Nutrient>> collect = nutrientQuestions.stream()
                    .collect(Collectors.groupingBy((nutrientQuestion ->
                                    nutrientQuestion.getQuestion().getCategory().getName()),
                            Collectors.mapping(NutrientQuestion::getNutrient, Collectors.toList())));
            collect.forEach((k, v) -> answers.merge(k, v, (v1, v2) -> {
                Set<Nutrient> combined = new HashSet<>(v1);
                combined.addAll(v2);
                return new ArrayList<>(combined);
            }));
        }
        return answers;
    }
}
