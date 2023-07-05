package com.mypill.domain.member.controller;

import com.mypill.domain.member.entity.Member;
import com.mypill.domain.member.exception.AlreadyJoinException;
import com.mypill.domain.member.form.JoinForm;
import com.mypill.domain.member.service.MemberService;
import com.mypill.global.rq.Rq;
import com.mypill.global.rsData.RsData;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@Controller
@RequiredArgsConstructor
@RequestMapping("/usr/member")
public class MemberController {

    private final MemberService memberService;
    private final Rq rq;

    @PreAuthorize("isAnonymous()")
    @GetMapping("/login")
    public String login(HttpServletRequest request, @RequestParam(value = "exception", required = false) String exception) {
        if (exception != null) {
            return rq.historyBack(exception);
        }
        String uri = request.getHeader("Referer");
        if (uri != null && !uri.contains("/usr/member/login")) {
            request.getSession().setAttribute("prevPage", uri);
        }
        return "usr/member/login";
    }

    @PreAuthorize("isAnonymous()")
    @GetMapping("/join")
    public String join() {
        return "usr/member/join";
    }

    @PreAuthorize("isAnonymous()")
    @PostMapping("/join")
    public String join(@Valid JoinForm joinForm) {
        try {
            memberService.join(joinForm.getUsername(), joinForm.getName(), joinForm.getPassword(),
                    Integer.parseInt(joinForm.getUserType()), joinForm.getEmail());
        } catch (AlreadyJoinException e) {
            return rq.historyBack(e.getMessage());
        } catch (NumberFormatException e) {
            return rq.historyBack("회원 유형이 올바르지 않습니다.");
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

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/deleteAccount")
    public void deleteAccount(HttpServletRequest request, HttpServletResponse response) throws IOException {
        RsData<Member> rsData = memberService.deleteAccount(rq.getMember());
        if (rsData.isFail()) {
            rq.historyBack(rsData.getMsg());
        }
        SecurityContextHolder.clearContext();
        request.getSession().invalidate();
        response.sendRedirect("/");
    }

    @PreAuthorize("isAuthenticated()")
    @ResponseBody
    @PostMapping("/name/update")
    public String nameUpdate(String newName) {
        RsData<Member> rsData = memberService.updateName(rq.getMember(), newName);
        return rsData.getResultCode();
    }
}
