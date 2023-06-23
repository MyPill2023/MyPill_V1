package com.mypill.domain.survey.service;

import com.mypill.domain.category.service.CategoryService;
import com.mypill.domain.survey.dto.SurveyRequestDto;
import com.mypill.domain.survey.dto.SurveyResponseDto;
import com.mypill.domain.survey.entity.Survey;
import com.mypill.domain.survey.repository.SurveyRepository;
import com.mypill.global.rsData.RsData;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class SurveyService {
    private final SurveyRepository surveyRepository;

    @Transactional
    public RsData<SurveyResponseDto> start(SurveyRequestDto surveyRequestDto) {
        Survey survey = Survey.of(surveyRequestDto);
        surveyRepository.save(survey);
        return RsData.of("S-1", "설문 1차 완료");
    }


}
