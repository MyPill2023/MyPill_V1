package com.mypill.domain.emailverification.controller;

import com.mypill.domain.member.service.MemberService;
import com.mypill.global.rq.Rq;
import com.mypill.global.rsData.RsData;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/emailVerification")
public class EmailVerificationController {
    private final MemberService memberService;
    private final Rq rq;

    @GetMapping("/verify")
    public String verify(Long memberId, String code) {
        RsData verifyEmailRsData = memberService.verifyEmail(memberId, code);
        if (verifyEmailRsData.isFail()) {
            return rq.redirectWithMsg("/", verifyEmailRsData);
        }
        String successMsg = verifyEmailRsData.getMsg();
        if (rq.isLogout()) {
            return rq.redirectWithMsg("/usr/member/login", successMsg);
        }
        return rq.redirectWithMsg("/", successMsg);
    }
}
