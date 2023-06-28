package com.mypill.domain.consumer.controller;

import com.mypill.domain.consumer.service.ConsumerService;
import com.mypill.global.rq.Rq;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/usr/consumer")
public class ConsumerController {
    private final ConsumerService consumerService;
    private final Rq rq;

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/myPage")
    public String showMyPage() {

        return "usr/consumer/myPage";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/infoUpdate")
    public String updateInfo() {

        return "usr/consumer/infoUpdate";
    }

}
