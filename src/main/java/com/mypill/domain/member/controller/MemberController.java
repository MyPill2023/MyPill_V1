package com.mypill.domain.member.controller;

import com.mypill.domain.member.dto.LoginRequestDto;
import com.mypill.domain.member.dto.LoginResponseDto;
import com.mypill.domain.member.entity.Member;
import com.mypill.domain.member.form.JoinForm;
import com.mypill.domain.member.service.MemberService;
import com.mypill.global.rq.Rq;
import com.mypill.global.rsData.RsData;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Controller
@ResponseBody
@RequiredArgsConstructor
@RequestMapping(value = "/usr/member", produces = APPLICATION_JSON_VALUE, consumes = APPLICATION_JSON_VALUE)
public class MemberController {

    private final MemberService memberService;
    private final Rq rq;

    @PreAuthorize("isAnonymous()")
    @GetMapping("/login")
    public String login() {
        return "usr/member/login";
    }

    @PostMapping("/login")
    public RsData<LoginResponseDto> login(@Valid @RequestBody LoginRequestDto loginRequestDto, HttpServletResponse resp) {
        String accessToken = memberService.genAccessToken(loginRequestDto.getUserId(), loginRequestDto.getPassword());
        return RsData.of(
                "S-1",
                "엑세스토큰이 생성되었습니다.",
                new LoginResponseDto(accessToken)
        );
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/logout")
    public String logout() {
        return "usr/member/logout";
    }

    @PreAuthorize("isAnonymous()")
    @GetMapping("/join")
    public String showJoin() {
        return "usr/member/join";
    }

    @PreAuthorize("isAnonymous()")
    @PostMapping("/join")
    public String join(@Valid JoinForm joinForm) {
        RsData<Member> rsData = memberService.join(joinForm.getUserId(), joinForm.getUsername(),
                joinForm.getPassword(), joinForm.getUserType(), joinForm.getEmail());
        if (rsData.isFail()) {
            return rq.historyBack(rsData.getMsg());
        }
        return rq.redirectWithMsg("/usr/member/login", rsData);
    }

    @PreAuthorize("isAnonymous()")
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

    @PreAuthorize("isAnonymous()")
    @ResponseBody
    @GetMapping("/join/emailCheck")
    public int emailCheck(String validEmail) {
        return memberService.isValidEmail(validEmail);
    }
}
