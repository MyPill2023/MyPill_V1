package com.mypill.domain.survey.dto.response;

import com.mypill.domain.category.entity.Category;
import com.mypill.domain.question.entity.Question;
import com.mypill.domain.survey.controller.StepParam;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class StepResponse {
    private StepParam stepParam;
    private List<Question> questions;
    private Category category;

    public static StepResponse of(StepParam stepParam, List<Question> questions, Category category) {
        return new StepResponse(stepParam, questions, category);
    }
}
