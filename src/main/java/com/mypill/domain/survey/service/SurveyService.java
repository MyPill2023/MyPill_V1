package com.mypill.domain.survey.service;

import com.mypill.domain.survey.entity.Survey;
import com.mypill.domain.survey.repository.SurveyRepository;
import com.mypill.global.rsData.RsData;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SurveyService {
    private final SurveyRepository surveyRepository;

//    public RsData<Survey> start(Long surveyId){
//
//        Survey survey = Survey.builder()
//                .member(id)
//                .surveyId(surveyId)
//                .build();
//
//        return RsData.of("S-1", "설문 1차 완료");
//    }


}
