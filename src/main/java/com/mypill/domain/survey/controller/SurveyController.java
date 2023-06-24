package com.mypill.domain.survey.controller;

import com.mypill.domain.category.entity.Category;
import com.mypill.domain.category.service.CategoryService;
import com.mypill.global.rq.Rq;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/usr/survey")
public class SurveyController {

    private final Rq rq;
    private final CategoryService categoryService;

    @GetMapping("/start")
    public String showStart(Model model) {

        List<Category> categories = categoryService.findAllByOrderByNameAsc();
        model.addAttribute("categories", categories);

        return "usr/survey/start";
    }

    @GetMapping("/step")
    public String showStep(Model model, @RequestParam(name = "category") List<Long> categoryIds, @RequestParam(defaultValue = "1") int stepNo) {

        //현재 스탭 넘버

        int maxStepsCount = categoryIds.size();

        List<StepQuestion> list = new ArrayList<>();
        list.add(new StepQuestion(1L, "Q1"));
        list.add(new StepQuestion(2L, "Q2"));

        model.addAttribute("stepQuestions", list);
        model.addAttribute("categoryIds", categoryIds);
        model.addAttribute("stepNo", stepNo);
        model.addAttribute("prevStepNo", stepNo - 1 >= 1 ? stepNo - 1 : 0);
        model.addAttribute("nextStepNo", stepNo + 1 <= maxStepsCount ? stepNo + 1 : maxStepsCount);
        model.addAttribute("isFirstStep", stepNo == 1);
        model.addAttribute("isLastStep", stepNo == maxStepsCount);

        return "usr/survey/step";
    }
}

@RequiredArgsConstructor
@Getter
class StepQuestion {
    private final Long id;
    private final String subject;
    private List<StepQuestionOption> options = new ArrayList<>() {{
        add(new StepQuestionOption(1L, "O1"));
        add(new StepQuestionOption(2L, "O2"));
        add(new StepQuestionOption(3L, "O3"));
    }};
}

@AllArgsConstructor
@Getter
class StepQuestionOption {
    private Long id;
    private String content;
}
