package com.mypill.domain.email.controller;

import com.mypill.domain.member.service.MemberService;
import com.mypill.global.rq.Rq;
import com.mypill.global.rsdata.RsData;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/emailVerification")
@Tag(name = "EmailVerificationController", description = "이메일 인증 수신 및 검증")
public class EmailVerificationController {
    private final MemberService memberService;
    private final Rq rq;

    @GetMapping("/verify")
    @Operation(summary = "이메일 인증 검증")
    public String verify(Long memberId, String code) {
        RsData verifyEmailRsData = memberService.verifyEmail(memberId, code);
        if (verifyEmailRsData.isFail()) {
            return rq.redirectWithMsg("/", verifyEmailRsData);
        }
        if (rq.isLogout()) {
            return rq.redirectWithMsg("/member/login", verifyEmailRsData);
        }
        return rq.redirectWithMsg("/", verifyEmailRsData);
    }
}
