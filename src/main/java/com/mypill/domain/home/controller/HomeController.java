package com.mypill.domain.home.controller;

import com.mypill.global.rq.Rq;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class HomeController {
    private final Rq rq;

    @GetMapping("/")
    public String showMain() {
        return "usr/home/main";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/myPage")
    public String myPage() {
        if (rq.isWaiter() || rq.isSeller()) {
            return "/usr/seller/myPage";
        }
        return "/usr/buyer/myPage";
    }
}
