package com.mypill.domain.survey.dto;

import com.mypill.domain.category.entity.Category;
import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Builder
@Data
public class SurveyResponseDto {

    private Long id;
    private List<Category> categories = new ArrayList<>();

    public List<Category> getCategories () {
        return categories;
    }

    public void setCategories (List<Category> categories) {
        this.categories = categories;
    }

    public static SurveyResponseDto of(SurveyRequestDto survey) {
        return SurveyResponseDto.builder()
                .id(survey.getId())
                .categories(new ArrayList<>())
                .build();
    }
}

