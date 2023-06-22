package com.mypill.domain.survey.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/usr/survey")
public class SurveyController {

    @GetMapping("/start")
    public String start() {
        return "usr/survey/start";
    }


}
