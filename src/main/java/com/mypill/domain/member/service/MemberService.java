package com.mypill.domain.member.service;

import com.mypill.domain.emailVerification.service.EmailVerificationService;
import com.mypill.domain.member.entity.Member;
import com.mypill.domain.member.exception.AlreadyJoinException;
import com.mypill.domain.member.repository.MemberRepository;
import com.mypill.domain.product.entity.Product;
import com.mypill.global.rsData.RsData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Slf4j
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailVerificationService emailVerificationService;


    @Transactional
    public RsData<Member> join(String username, String name, String password, String userTypeStr, String email) {
        if (memberRepository.findByUsername(username).isPresent()) {
            throw new AlreadyJoinException("%s(은)는 이미 사용중인 아이디 입니다.".formatted(username));
        }

        Integer userType = Integer.parseInt(userTypeStr);
        Member member = Member.builder()
                .username(username)
                .name(name)
                .password(passwordEncoder.encode(password))
                .email(email)
                .userType(userType)
                .build();
        Member savedMember = memberRepository.save(member);

        CompletableFuture<RsData<Long>> sendRsFuture = emailVerificationService.send(member);
        sendRsFuture.whenComplete((sendRs, throwable) -> {
            if (sendRs.isSuccess()) {
                log.info("이메일 인증 메일 발송 성공");
            } else {
                log.info("이메일 인증 메일 발송 실패");
            }
        });
        return RsData.of("S-1", "회원가입이 완료되었습니다.", savedMember);
    }

    private RsData<Member> oauthJoin(String providerTypeCode, String username, String password, String name, String email) {
        if (findByUsername(username).isPresent()) {
            return RsData.of("F-1", "해당 아이디(%s)는 이미 사용중입니다.".formatted(username));
        }

        if (StringUtils.hasText(password)) {
            password = passwordEncoder.encode(password);
        }

        Member member = Member.builder()
                .providerTypeCode(providerTypeCode)
                .username(username)
                .name(name)
                .password(passwordEncoder.encode(password))
                .userType(1)
                .email(email)
                .build();

        memberRepository.save(member);
        return RsData.of("S-1", "회원가입이 완료되었습니다.", member);
    }

    public Optional<Member> findByUsername(String username) {
        return memberRepository.findByUsername(username);
    }

    public Optional<Member> findById(Long id) {
        return memberRepository.findById(id);
    }


    public int idValidation(String username) {
        if (username.equals("")) {
            return 1;
        }
        if (memberRepository.findByUsername(username).isPresent()) {
            return 2;
        }
        return 0;
    }

    public int emailValidation(String email) {
        if (email.equals("")) {
            return 1;
        }
        if (!isValidEmailForm(email)) {
            return 2;
        }
        if (memberRepository.findByEmail(email).isPresent()) {
            return 3;
        }
        return 0;
    }

    private boolean isValidEmailForm(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\." +
                "[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                "A-Z]{2,7}$";
        Pattern pattern = Pattern.compile(emailRegex);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    @Transactional
    public RsData<Member> whenSocialLogin(String providerTypeCode, String username, String name, String email) {
        Optional<Member> opMember = findByUsername(username);

        return opMember.map(member -> RsData.of("S-2", "로그인 되었습니다.", member))
                .orElseGet(() -> oauthJoin(providerTypeCode, username, "", name, email));

    }

    public boolean verifyWithWhiteList(Member member, String token) {
        return member.getAccessToken().equals(token);
    }

    public void whenAfterLike(Member member, Product product) {
        member.like(product);
    }

    public void whenAfterUnlike(Member member, Product product) {
        member.unLike(product);
    }

    @Transactional
    public RsData<Member> surveyDelete(Member member) {
        member.getSurveyNutrients().clear();
        return RsData.of("S-1", "설문이 초기화 되었습니다");
    }

    @Transactional
    public String nameUpdate(Member member, String newName) {
        member.updateName(newName);
        memberRepository.save(member);
        return "success";
    }

    @Transactional
    public RsData deleteAccount(Member member) {
        if (member == null) {
            return RsData.of("F-1", "로그인이 필요한 서비스입니다.");
        }
        memberRepository.delete(member);
        return RsData.of("S-1", "회원 탈퇴가 완료되었습니다.");
    }

    @Transactional
    public RsData verifyEmail(Long id, String verificationCode) {
        RsData verifyVerificationCodeRs = emailVerificationService.verifyVerificationCode(id, verificationCode);

        if (verifyVerificationCodeRs.isSuccess() == false) {
            return verifyVerificationCodeRs;
        }

        Member member = memberRepository.findById(id).get();
        member.setEmailVerified(true);

        return RsData.of("S-1", "이메일인증이 완료되었습니다.");
    }

    public boolean hasEmailVerified(String username) {
        Member member = memberRepository.findByUsername(username).orElse(null);
        if (member == null) {
            return false;
        }
        return member.getEmailVerified();
    }
}