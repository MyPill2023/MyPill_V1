package com.mypill.domain.emailverification.service;

import com.mypill.domain.attr.service.AttrService;
import com.mypill.domain.email.service.EmailService;
import com.mypill.domain.member.entity.Member;
import com.mypill.global.AppConfig;
import com.mypill.global.rsdata.RsData;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class EmailVerificationService {
    private final EmailService emailService;
    private final AttrService attrService;

    @Async
    public CompletableFuture<RsData<Long>> send(Member member) {
        String email = member.getEmail();
        String title = "[%s 이메일인증] 안녕하세요 %s님. 링크를 클릭하여 회원가입을 완료해주세요.".formatted(AppConfig.getSiteName(), member.getName());
        String url = genEmailVerificationUrl(member);
        String body = "해당 링크가 열리지 않는다면, 링크를 복사하여 주소창에 검색해주세요.\n" + url;
        RsData<Long> sendEmailRs = emailService.sendEmail(email, title, body);
        return CompletableFuture.supplyAsync(() -> sendEmailRs);
    }

    public String genEmailVerificationUrl(Member member) {
        return genEmailVerificationUrl(member.getId());
    }

    public String genEmailVerificationUrl(long memberId) {
        String code = genEmailVerificationCode(memberId);
        return AppConfig.getSiteBaseUrl() + "/emailVerification/verify?memberId=%d&code=%s".formatted(memberId, code);
    }

    public String genEmailVerificationCode(long memberId) {
        String code = UUID.randomUUID().toString();
        attrService.set("member__%d__extra__emailVerificationCode".formatted(memberId), code, LocalDateTime.now().plusSeconds(60 * 60));
        return code;
    }

    public RsData verifyVerificationCode(long memberId, String code) {
        String foundCode = attrService.get("member__%d__extra__emailVerificationCode".formatted(memberId), "");
        if (!foundCode.equals(code)) {
            return RsData.of("F-1", "만료되었거나 유효하지 않은 코드입니다.");
        }
        return RsData.of("S-1", "인증된 코드 입니다.");
    }
}
