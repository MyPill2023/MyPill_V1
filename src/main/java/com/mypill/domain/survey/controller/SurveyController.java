package com.mypill.domain.survey.controller;

import com.mypill.domain.category.entity.Category;
import com.mypill.domain.category.service.CategoryService;
import com.mypill.domain.survey.dto.SurveyRequestDto;
import com.mypill.domain.survey.dto.SurveyResponseDto;
//import com.mypill.domain.survey.service.SurveyService;
import com.mypill.domain.survey.service.SurveyService;
import com.mypill.global.rq.Rq;
import com.mypill.global.rsData.RsData;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/usr/survey")
public class SurveyController {

    private final Rq rq;
    private final CategoryService categoryService;
    private final SurveyService surveyService;

    @GetMapping("/start")
    public String showStart(Model model) {

        List<Category> categories = categoryService.findAllByOrderByNameAsc();
        model.addAttribute("categories", categories);

        return "usr/survey/start";
    }

    @PostMapping("/start")
    public String start(@Valid SurveyRequestDto surveyRequestDto){

        RsData<SurveyResponseDto> surveyStartRsData = surveyService.start(surveyRequestDto);

        return rq.redirectWithMsg("/usr/survey/step/%s".formatted(surveyStartRsData.getData().getId()), surveyStartRsData);
    }

}
