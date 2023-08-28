package com.mypill.domain.survey.dto.response;

import com.mypill.domain.category.dto.response.CategoryResponse;
import com.mypill.domain.category.entity.Category;
import com.mypill.domain.question.dto.response.QuestionResponse;
import com.mypill.domain.question.entity.Question;
import com.mypill.domain.survey.controller.StepParam;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class StepResponse {
    private StepParam stepParam;
    private List<QuestionResponse> questions;
    private CategoryResponse category;

    public static StepResponse of(StepParam stepParam, List<Question> questions, Category category) {
        return StepResponse.builder()
                .stepParam(stepParam)
                .questions(questions.stream().map(QuestionResponse::of).toList())
                .category(CategoryResponse.of(category))
                .build();
    }
}
