package com.mypill.domain.survey.controller;

import com.mypill.domain.survey.service.SurveyService;
import com.mypill.global.rq.Rq;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;

@Controller
@RequiredArgsConstructor
@RequestMapping("/usr/survey")
public class SurveyController {

    private final Rq rq;
    private final SurveyService surveyService;

    @GetMapping("/start")
    public String showStart() {

        //맴버아이디 가져오기

        return "usr/survey/start";
    }

    @PostMapping("/start")
    public String start(){

        LinkedHashMap<Long, String> map = new LinkedHashMap<>();
        map.put(1L,"눈 건강");
        map.put(2L,"면역력 / 항산화");
        map.put(3L,"뼈, 관절 건강");
        map.put(4L,"위(소화)");
        map.put(5L,"두뇌 활동");
        map.put(6L,"피부");
        map.put(7L,"다이어트");
        map.put(8L,"수면");
        map.put(9L,"간");
        map.put(10L,"대장");

        return "usr/survey/start";
    }



}
