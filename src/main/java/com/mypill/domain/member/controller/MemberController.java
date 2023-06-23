package com.mypill.domain.member.controller;

import com.mypill.domain.member.entity.Member;
import com.mypill.domain.member.form.JoinForm;
import com.mypill.domain.member.service.MemberService;
import com.mypill.global.rq.Rq;
import com.mypill.global.rsData.RsData;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequiredArgsConstructor
@RequestMapping("/usr/member")
public class MemberController {

    private final MemberService memberService;
    private final Rq rq;

    @GetMapping("/login")
    public String showLogin() {
        return "usr/member/login";
    }


    @GetMapping("/join")
    public String showJoin() {
        return "usr/member/join";
    }

    @PostMapping("/join")
    public String join(@Valid JoinForm joinForm) {
        RsData<Member> rsData = memberService.join(joinForm.getUserId(), joinForm.getUserType(),
                joinForm.getUsername(), joinForm.getPassword(), joinForm.getEmail());
        if (rsData.isFail()) {
            return rq.historyBack(rsData.getMsg());
        }
        return rq.redirectWithMsg("/usr/member/login", rsData);
    }

    @ResponseBody
    @GetMapping("/join/idCheck")
    public int idCheck(String validUserId) {
        if (validUserId.equals("")) {
            return 1;
        }
        if (memberService.isIdDuplicated(validUserId)) {
            return 2;
        }
        return 0;
    }

    @ResponseBody
    @GetMapping("/join/emailCheck")
    public int emailCheck(String validEmail) {
        return memberService.isValidEmail(validEmail);
    }

    @GetMapping("/login/auth/fail")
    public String authLoginFail() {
        RsData rsData = memberService.getErrorMsg(rq.getUserId(), rq.getPassword());
        return rq.historyBack(rsData.getMsg());
    }

}
