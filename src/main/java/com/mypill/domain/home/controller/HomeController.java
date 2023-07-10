package com.mypill.domain.home.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
@Tag(name = "HomeController", description = "홈")
public class HomeController {

    @GetMapping("/")
    @Operation(summary = "메인 페이지")
    public String showMain() {
        return "usr/home/main";
    }
}
