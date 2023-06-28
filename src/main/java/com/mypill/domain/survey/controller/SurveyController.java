package com.mypill.domain.survey.controller;

import com.mypill.domain.category.entity.Category;
import com.mypill.domain.category.service.CategoryService;
import com.mypill.domain.nutrient.Service.NutrientService;
import com.mypill.domain.nutrient.entity.Nutrient;
import com.mypill.domain.question.entity.NutrientQuestion;
import com.mypill.domain.question.entity.Question;
import com.mypill.domain.question.service.NutrientQuestionService;
import com.mypill.domain.question.service.QuestionService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
@RequestMapping("/usr/survey")
@RequiredArgsConstructor
public class SurveyController {
    private final CategoryService categoryService;
    private final QuestionService questionService;
    private final NutrientQuestionService nutrientQuestionService;
    private final NutrientService nutrientService;

    @GetMapping("/start")
    public String start(Model model) {
        List<Category> categoryItems = categoryService.findAll();
        model.addAttribute("categoryItems", categoryItems);

        return "usr/survey/start";
    }

    @GetMapping("/step")
    public String step(Model model, @RequestParam Map<String, String> param, @RequestParam(defaultValue = "1") Long stepNo) {
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
    public String complete(Model model, @RequestParam Map<String, String> param) {
        StepParam stepParam = new StepParam(param, 1L);

        Long[] questionIds = stepParam.getQuestionIds();
        Set<Long> answers = new HashSet<>();
        for (Long id : questionIds) {
            List<NutrientQuestion> nutrients = nutrientQuestionService.findByNutrientId(id);

            for (NutrientQuestion nutirent : nutrients){
                answers.add(nutirent.getId());
            }
        }

        List<List<String>> nutrientAnswers = new ArrayList<>();
        for (Long id : answers) {
            Optional<Nutrient> nutrient = nutrientService.findById(id);

            if (nutrient.isPresent()){
                List<String> nutrientData = new ArrayList<>();
                String name = nutrient.get().getName();
                String description = nutrient.get().getDescription();
                nutrientData.add(name);
                nutrientData.add(description);
                nutrientAnswers.add(nutrientData);
            }
        }
        for (List<String> d: nutrientAnswers){
            System.out.println(d);
        }

        model.addAttribute("nutrientAnswers",nutrientAnswers);

        return "usr/survey/complete";
    }
}

@Getter
@ToString
class StepParam {
    private final Map<String, String> param;
    private final Long stepNo;
    private final Long[] categoryItemIds;
    private final Long[] questionIds;
    private final boolean isFirst;
    private final boolean isLast;

    public StepParam (Map<String, String> param, Long stepNo) {
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
