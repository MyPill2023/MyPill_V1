package com.mypill.domain.survey.controller;

import com.mypill.domain.category.dto.response.CategoriesResponse;
import com.mypill.domain.category.entity.Category;
import com.mypill.domain.category.service.CategoryService;
import com.mypill.domain.nutrient.entity.Nutrient;
import com.mypill.domain.question.entity.Question;
import com.mypill.domain.question.service.QuestionService;
import com.mypill.domain.survey.dto.response.StepResponse;
import com.mypill.domain.survey.service.SurveyService;
import com.mypill.global.rq.Rq;
import com.mypill.global.rsdata.RsData;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
@RequestMapping("/survey")
@RequiredArgsConstructor
@Tag(name = "SurveyController", description = "설문조사")
public class SurveyController {
    private final CategoryService categoryService;
    private final QuestionService questionService;
    private final Rq rq;
    private final SurveyService surveyService;

    @PreAuthorize("hasAuthority('BUYER') or isAnonymous()")
    @GetMapping("/guide")
    @Operation(summary = "설문 가이드 페이지")
    public String showGuide() {
        if (rq.isLogin()) {
            return "redirect:/survey/start";
        }
        return "usr/survey/guide";
    }

    @PreAuthorize("hasAuthority('BUYER') or isAnonymous()")
    @GetMapping("/start")
    @Operation(summary = "설문 시작 페이지")
    public String showStart(Model model) {
        List<Category> categories = categoryService.findAll();
        model.addAttribute("categoriesResponse", CategoriesResponse.of(categories));
        return "usr/survey/start";
    }

    @PreAuthorize("hasAuthority('BUYER') or isAnonymous()")
    @GetMapping("/step")
    @Operation(summary = "설문 질문 페이지")
    public String showStep(Model model, @RequestParam Map<String, String> param, @RequestParam(defaultValue = "1") Long stepNo) {
        StepParam stepParam = new StepParam(param, stepNo);
        Long categoryItemId = stepParam.getCategoryItemId();
        RsData<String> startSurveyRsData = surveyService.validStartSurvey(stepParam.getCategoryItemIds());
        if (startSurveyRsData.isFail()) {
            return rq.redirectWithMsg("/survey/start", startSurveyRsData);
        }
        List<Question> questions = questionService.findByCategoryId(categoryItemId);
        Category category = categoryService.getCategory(categoryItemId);
        model.addAttribute("stepResponse", StepResponse.of(stepParam, questions, category));
        return "usr/survey/step";
    }

    @PreAuthorize("hasAuthority('BUYER') or isAnonymous()")
    @Transactional
    @GetMapping("/complete")
    @Operation(summary = "설문 결과 페이지")
    public String showComplete(Model model, @RequestParam Map<String, String> param) {
        StepParam stepParam = new StepParam(param, 1L);
        Long[] questionIds = stepParam.getQuestionIds();
        RsData<String> completeSurveyRsData = surveyService.validCompleteSurvey(questionIds);
        if (completeSurveyRsData.isFail()) {
            return rq.redirectWithMsg("/survey/step", completeSurveyRsData);
        }
        Map<String, List<Nutrient>> answers = surveyService.getAnswers(questionIds);
        if (rq.isLogin()) {
            surveyService.extracted(answers, rq.getMember());
        }
        model.addAttribute("answers", answers);
        return "usr/survey/complete";
    }
}
