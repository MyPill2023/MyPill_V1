package com.mypill.domain.question.controller;

import com.mypill.domain.question.dto.QuestionCreateRequest;
import com.mypill.domain.question.entity.Question;
import com.mypill.domain.question.service.QuestionService;
import com.mypill.global.rq.Rq;
import com.mypill.global.rsData.RsData;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@RequestMapping("/question")
@RequiredArgsConstructor
@Controller
@Tag(name = "QuestionController", description = "질문게시판")
public class QuestionController {
    private final QuestionService questionService;
    private final Rq rq;

    @GetMapping("/list")
    @Operation(summary = "질문 목록")
    public String showList(Model model) {
        List<Question> questionList = questionService.getList();
        model.addAttribute("questionList", questionList);

        return "usr/question/list";
    }

    @GetMapping("/create")
    @Operation(summary = "질문 등록 폼")
    public String create(Model model) {
        model.addAttribute("QuestionCreateRequest", new QuestionCreateRequest());

        return "usr/question/create";
    }

    @PostMapping("/create")
    @Operation(summary = "질문 등록")
    public String create(@ModelAttribute QuestionCreateRequest req) {
        RsData<Question> createRsData = questionService.create(req);

        return rq.redirectWithMsg("/question/list", createRsData);
    }
}
