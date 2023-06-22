package com.mypill.domain.member.controller;

import com.mypill.domain.member.entity.Member;
import com.mypill.domain.member.service.MemberService;
import com.mypill.global.rq.Rq;
import com.mypill.global.rsData.RsData;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

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

    @PostMapping("/login")
    public String login(String userId, String password) {
        RsData<Member> rsData = memberService.login(userId, password);
        if (rsData.isFail()) {
            return rq.historyBack(rsData.getMsg());
        }
        return rq.redirectWithMsg("/",rsData);
    }

    @GetMapping("/join")
    public String showJoin() {
        return "usr/member/join";
    }

    @PostMapping("/join")
    public String join(String userId, String username, String password, String email) {
        RsData<Member> rsData = memberService.join(userId, username, password, email);
        if (rsData.isFail()) {
            return rq.historyBack(rsData.getMsg());
        }
        return rq.redirectWithMsg("/",rsData);
    }
}
