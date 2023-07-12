package com.mypill.domain.survey.service;

import com.mypill.global.AppConfig;
import com.mypill.global.rsdata.RsData;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SurveyService {

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
}
