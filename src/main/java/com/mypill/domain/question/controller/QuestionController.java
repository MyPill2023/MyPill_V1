package com.mypill.domain.question.controller;

import com.mypill.domain.product.dto.response.ProductResponse;
import com.mypill.domain.question.entity.Question;
import com.mypill.domain.question.form.QuestionForm;
import com.mypill.domain.question.service.QuestionService;
import com.mypill.global.rq.Rq;
import com.mypill.global.rsData.RsData;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
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
    public String create(QuestionForm questionForm) {
        return "usr/question/create";
    }

    @PostMapping("/create")
    public String create(@Valid QuestionForm questionForm, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return "usr/board/board_form";
        }

        Member member = memberService.getMember(member.getUsername());

        RsData<Question> createRsData = questionService.create(questionForm);

        return rq.redirectWithMsg("/question/detail/%s".formatted(createRsData.getData().getId()), createRsData);
    }

//
//    @GetMapping("/detail/{id}")
//    public String detail(Model model, @PathVariable("id") Long id, CommentForm commentForm){
//        Board board = this.boardService.getBoard(id);
//        model.addAttribute("board", board);
//        return "usr/board/board_detail";
//    }
}
