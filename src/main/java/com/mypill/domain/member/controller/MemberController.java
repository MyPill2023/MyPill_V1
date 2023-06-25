package com.mypill.domain.member.controller;

import com.mypill.domain.member.exception.AlreadyJoinException;
import com.mypill.domain.member.form.JoinForm;
import com.mypill.domain.member.service.MemberService;
import com.mypill.global.rq.Rq;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/usr/member")
public class MemberController {

    private final MemberService memberService;
    private final Rq rq;

    @PreAuthorize("isAnonymous()")
    @GetMapping("/login")
    public String login(HttpServletRequest request) {
        String uri = request.getHeader("Referer");
        if (uri != null && !uri.contains("/usr/member/login")) {
            request.getSession().setAttribute("prevPage", uri);
        }
        return "usr/member/login";
    }

    @PreAuthorize("isAnonymous()")
    @GetMapping("/join")
    public String showJoin() {
        return "usr/member/join";
    }

    @PreAuthorize("isAnonymous()")
    @PostMapping("/join")
    public String join(@Valid JoinForm joinForm) {
        try {
            memberService.join(joinForm.getUsername(), joinForm.getName(),
                    joinForm.getPassword(), joinForm.getUserType(), joinForm.getEmail());
        } catch (AlreadyJoinException e) {
            return rq.historyBack(e.getMessage());
        }
        return rq.redirectWithMsg("/usr/member/login", "회원가입이 완료되었습니다.");
    }

    @ResponseBody
    @GetMapping("/join/idCheck")
    public int idCheck(String username) {
        return memberService.idValidation(username);
    }

    @ResponseBody
    @GetMapping("/join/emailCheck")
    public int emailCheck(String email) {
        return memberService.emailValidation(email);
    }
}
