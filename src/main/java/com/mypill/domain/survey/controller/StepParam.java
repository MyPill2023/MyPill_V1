package com.mypill.domain.survey.controller;

import lombok.Getter;
import lombok.ToString;

import java.util.Map;

@Getter
@ToString
public class StepParam {
    private final Map<String, String> param;
    private final Long stepNo;
    private final Long[] categoryItemIds;
    private final Long[] questionIds;
    private final boolean isFirst;
    private final boolean isLast;

    public StepParam(Map<String, String> param, Long stepNo) {
        this.param = param;
        this.stepNo = stepNo;
        categoryItemIds = param
                .keySet()
                .stream()
                .filter(key -> key.startsWith("category_"))
                .map(key -> Long.parseLong(key.replace("category_", "")))
                .sorted()
                .toArray(Long[]::new);
        questionIds = param
                .keySet()
                .stream()
                .filter(key -> key.startsWith("question_"))
                .map(key -> Long.parseLong(key.replace("question_", "")))
                .sorted()
                .toArray(Long[]::new);
        isFirst = stepNo == 1L;
        isLast = stepNo == categoryItemIds.length;
    }

    public Long getCategoryItemId() {
        return categoryItemIds[stepNo.intValue() - 1];
    }

    public Long getNextStepNo() {
        return stepNo + 1L;
    }

    public Long getPrevStepNo() {
        return stepNo - 1L;
    }

    public boolean isChecked(Long questionId) {
        return param.containsKey("question_" + questionId);
    }
}
