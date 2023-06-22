package com.mypill.domain.board.controller;

import com.mypill.domain.board.entity.Board;
import com.mypill.domain.board.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@RequestMapping("/board")
@RequiredArgsConstructor
@Controller
public class BoardController {
    private final BoardService boardService;

    @PreAuthorize("isAnonymous()")
    @GetMapping("/list")
    public String showList(Model model) {
        List<Board> boardList = boardService.getList();
        model.addAttribute("boardList", boardList);
        return "board/list";
    }
}
