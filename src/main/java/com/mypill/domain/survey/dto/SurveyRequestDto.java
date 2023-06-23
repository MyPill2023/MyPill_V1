package com.mypill.domain.survey.dto;
import com.mypill.domain.category.entity.Category;
import com.mypill.domain.survey.entity.Survey;
import lombok.Data;

import java.util.List;

@Data
public class SurveyRequestDto {

    private Long id;
    private List<Category> categories;

}
