package com.mypill.domain.question.controller;

import com.mypill.domain.question.entity.Question;
import com.mypill.domain.question.service.QuestionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "QuestionController", description = "질문게시판")
public class QuestionController {
    private final QuestionService questionService;

    @GetMapping("/list")
    @Operation(summary = "질문 목록")
    public String showList(Model model) {
        List<Question> questionList = questionService.getList();
        model.addAttribute("questionList", questionList);
        return "usr/question/list";
    }
}
