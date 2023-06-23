package com.mypill.domain.question.controller;

import com.mypill.domain.question.entity.Question;
import com.mypill.domain.question.service.QuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequestMapping("/question")
@RequiredArgsConstructor
@RestController
public class QuestionController {
    private final QuestionService questionService;

    @PreAuthorize("isAnonymous()")
    @GetMapping("/list")
    public String showList(Model model) {
        List<Question> questionList = questionService.getList();
        model.addAttribute("questionList", questionList);
        return "usr/question/list";
    }
}
