package com.mypill.domain.survey.entity;

import com.mypill.domain.category.entity.Category;
import com.mypill.domain.category.service.CategoryService;
import com.mypill.domain.survey.dto.SurveyRequestDto;
import com.mypill.global.base.entitiy.BaseEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Transient;

import java.util.ArrayList;
import java.util.List;



@Entity
@Getter
@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Survey extends BaseEntity {

    @OneToMany(mappedBy = "Survey",cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    private List<Category> categories;

    public static Survey of (SurveyRequestDto surveyRequestDto) {
        return Survey.builder()
                .id(surveyRequestDto.getId())
                .categories(surveyRequestDto.getCategories())
                .build();
    }

}
