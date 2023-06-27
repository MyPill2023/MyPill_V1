package com.mypill.domain.survey.controller;

import com.mypill.domain.category.entity.Category;
import com.mypill.domain.category.service.CategoryService;
import com.mypill.domain.question.entity.Question;
import com.mypill.domain.question.service.QuestionService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/usr/survey")
@RequiredArgsConstructor
public class SurveyController {
    private final CategoryService categoryService;
    private final QuestionService questionService;

    @GetMapping("/start")
    public String start(Model model) {
        List<Category> categoryItems = categoryService.findAll();
        model.addAttribute("categoryItems", categoryItems);

        return "usr/survey/start";
    }

    @GetMapping("/step")
    public String step(Model model, @RequestParam Map<String, String> param, @RequestParam(defaultValue = "1") int stepNo) {
        StepParam stepParam = new StepParam(param, stepNo);

        Long categoryItemId = stepParam.getCategoryItemId();

        List<Question> questions = questionService.findByCategoryId(categoryItemId);
        Optional<Category> category = categoryService.findById(categoryItemId);
        model.addAttribute("questions", questions);
        model.addAttribute("category",category);
        model.addAttribute("stepParam", stepParam);

        return "usr/survey/step";
    }

    @PostMapping("/complete")
    @ResponseBody
    public StepParam complete(@RequestParam Map<String, String> param) {
        StepParam stepParam = new StepParam(param, 1);

        return stepParam;
    }
}

@Getter
@ToString
class StepParam {
    private final Map<String, String> param;
    private final int stepNo;
    private final int[] categoryItemIds;
    private final int[] questionIds;
    private final boolean isFirst;
    private final boolean isLast;

    public StepParam(Map<String, String> param, int stepNo) {
        this.param = param;
        this.stepNo = stepNo;
        categoryItemIds = param
                .keySet()
                .stream()
                .filter(key -> key.startsWith("category_"))
                .mapToInt(key -> Integer.parseInt(key.replace("category_", "")))
                .sorted()
                .toArray();

        questionIds = param
                .keySet()
                .stream()
                .filter(key -> key.startsWith("question_"))
                .mapToInt(key -> Integer.parseInt(key.replace("question_", "")))
                .sorted()
                .toArray();

        isFirst = stepNo == 1;
        isLast = stepNo == categoryItemIds.length;
    }

    public Long getCategoryItemId() {
        return (long) categoryItemIds[stepNo - 1];
    }

    public int getNextStepNo() {
        return stepNo + 1;
    }

    public int getPrevStepNo() {
        return stepNo - 1;
    }

    public boolean isChecked(Long questionId) {
        return param.containsKey("question_" + questionId);
    }

}
