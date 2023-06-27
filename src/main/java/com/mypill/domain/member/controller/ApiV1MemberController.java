package com.mypill.domain.member.controller;

import com.mypill.domain.member.dto.LoginRequest;
import com.mypill.domain.member.dto.LoginResponse;
import com.mypill.domain.member.entity.Member;
import com.mypill.domain.member.service.MemberService;
import com.mypill.global.rq.Rq;
import com.mypill.global.rsData.RsData;
import com.mypill.global.util.Ut;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.MediaType.ALL_VALUE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(value = "/api/v1/usr/member", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@Slf4j
@Tag(name = "ApiV1MemberController", description = "로그인 기능과 로그인 된 회원의 정보를 제공 기능을 담당")
public class ApiV1MemberController {
    private final MemberService memberService;
    private final PasswordEncoder passwordEncoder;
    private final Rq rq;

    @PostMapping("/login")
    @Operation(summary = "로그인, 엑세스 토큰 발급")
    public ResponseEntity<RsData<LoginResponse>> login(@Valid @RequestBody LoginRequest loginRequest) {
        Member member = memberService.findByUsername(loginRequest.getUsername()).orElse(null);

        if (member == null) {
            return Ut.sp.responseEntityOf(
                    RsData.of(
                            "F-2",
                            "일치하는 회원이 존재하지 않습니다."
                    )
            );
        }

        if (passwordEncoder.matches(loginRequest.getPassword(), member.getPassword()) == false) {
            return Ut.sp.responseEntityOf(
                    RsData.of(
                            "F-3",
                            "비밀번호가 일치하지 않습니다."
                    )
            );
        }

        log.debug("Util.json.toStr(member.getAccessTokenClaims()) : " + Ut.json.toStr(member.getAccessTokenClaims()));

        String accessToken = memberService.genAccessToken(member);

        return Ut.sp.responseEntityOf(
                RsData.of(
                        "S-1",
                        "로그인 성공, Access Token을 발급합니다.",
                        new LoginResponse(accessToken)
                )
        );
    }
}