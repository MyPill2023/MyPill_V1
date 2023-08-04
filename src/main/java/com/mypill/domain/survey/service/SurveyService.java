package com.mypill.domain.survey.service;

import com.mypill.domain.member.entity.Member;
import com.mypill.domain.nutrient.entity.Nutrient;
import com.mypill.domain.question.entity.NutrientQuestion;
import com.mypill.domain.question.service.NutrientQuestionService;
import com.mypill.domain.survey.properties.SurveyProperties;
import com.mypill.global.rsdata.RsData;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SurveyService {
    private final NutrientQuestionService nutrientQuestionService;
    private final SurveyProperties surveyProperties;

    public RsData<String> validStartSurvey(Long[] categoryItemIds) {
        if (categoryItemIds.length > surveyProperties.getStartMaxLength()) {
            return RsData.of("F-1", "%d개 이하로 선택해주세요.".formatted(surveyProperties.getStartMaxLength()));
        }
        if (categoryItemIds.length < surveyProperties.getStartMinLength()) {
            return RsData.of("F-2", "%d개 이상으로 선택해주세요.".formatted(surveyProperties.getStartMinLength()));
        }
        return RsData.of("S-1", "설문 완료");
    }

    public RsData<String> validCompleteSurvey(Long[] questionIds) {
        if (questionIds.length > surveyProperties.getCompleteMaxLength()) {
            return RsData.of("F-1", "%d개 이하로 선택해주세요.".formatted(surveyProperties.getCompleteMaxLength()));
        }
        if (questionIds.length < surveyProperties.getCompleteMinLength()) {
            return RsData.of("F-2", "%d개 이상으로 선택해주세요.".formatted(surveyProperties.getCompleteMinLength()));
        }
        return RsData.of("S-1", "질문 완료");
    }

    public Map<String, List<Nutrient>> getAnswers(Long[] questionIds) {
        return Arrays.stream(questionIds).map(nutrientQuestionService::findByQuestionId)
                .flatMap(Collection::stream)
                .collect(Collectors.groupingBy(
                        nutrientQuestion -> nutrientQuestion.getQuestion().getCategory().getName(),
                        Collectors.mapping(NutrientQuestion::getNutrient, Collectors.toSet())
                ))
                .entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> new ArrayList<>(entry.getValue())
                ));
    }

    @Transactional
    public void extracted(Map<String, List<Nutrient>> answers, Member member) {
        List<Nutrient> allNutrients = new ArrayList<>();
        answers.values().forEach(allNutrients::addAll);
        member.setSurveyNutrients(allNutrients);
    }
}
