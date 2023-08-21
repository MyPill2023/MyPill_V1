package com.mypill.domain.member.service;

import com.mypill.domain.email.service.EmailVerificationService;
import com.mypill.domain.member.dto.request.JoinRequest;
import com.mypill.domain.member.entity.Member;
import com.mypill.domain.member.entity.Role;
import com.mypill.domain.member.repository.MemberRepository;
import com.mypill.domain.member.validation.EmailValidationResult;
import com.mypill.domain.member.validation.UsernameValidationResult;
import com.mypill.global.event.EventAfterDeleteMember;
import com.mypill.global.rsdata.RsData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Slf4j
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailVerificationService emailVerificationService;
    private final ApplicationEventPublisher publisher;

    @Transactional
    public RsData<Member> join(JoinRequest joinRequest, boolean emailVerified) {
        Member member = Member.of(joinRequest, passwordEncoder.encode(joinRequest.getPassword()), emailVerified);
        memberRepository.save(member);
        return RsData.of("S-1", "회원가입이 완료되었습니다.", member);
    }

    @Transactional
    public RsData<Member> join(JoinRequest joinRequest) {
        if (memberRepository.findByUsername(joinRequest.getUsername()).isPresent()) {
            return RsData.of("F-1", "%s(은)는 이미 사용중인 아이디 입니다.".formatted(joinRequest.getUsername()));
        }
        Member member = Member.of(joinRequest, passwordEncoder.encode(joinRequest.getPassword()));
        memberRepository.save(member);
        emailVerificationService.send(member);
        return RsData.of("S-1", "회원가입이 완료되었습니다.", member);
    }

    @Transactional
    public RsData<Member> whenSocialLogin(String providerTypeCode, String username, String name, String email) {
        Member member = findByUsername(username).orElse(null);
        if (member == null) {
            member = oauthJoin(providerTypeCode, username, name, email).getData();
        }
        return RsData.of("S-2", "로그인 되었습니다.", member);
    }

    @Transactional
    public RsData<Member> updateName(Member member, String newName) {
        member.updateName(newName);
        return RsData.of("S-1", "이름 변경이 완료되었습니다.", member);
    }

    @Transactional
    public RsData<Member> softDelete(Member member) {
        member.softDelete();
        publisher.publishEvent(new EventAfterDeleteMember(this, member));
        return RsData.of("S-1", "회원 탈퇴가 완료되었습니다.", member);
    }

    @Transactional
    public void hardDelete(Member member) {
        memberRepository.delete(member);
    }

    @Transactional
    public RsData verifyEmail(Long id, String verificationCode) {
        RsData verifyVerificationCodeRs = emailVerificationService.verifyVerificationCode(id, verificationCode);
        if (!verifyVerificationCodeRs.isSuccess()) {
            return verifyVerificationCodeRs;
        }
        Member member = findById(id).orElse(null);
        if (member == null) {
            return RsData.of("F-1", "이메일 인증에 실패했습니다.");
        }
        member.emailVerify();
        return RsData.of("S-1", "이메일 인증이 완료되었습니다.");
    }

    public Optional<Member> findById(Long id) {
        return memberRepository.findById(id);
    }

    public Optional<Member> findByUsername(String username) {
        return memberRepository.findByUsername(username);
    }

    public Optional<Member> findByEmail(String email) {
        return memberRepository.findByEmail(email);
    }

    public List<Member> getUnverifiedMember() {
        return memberRepository.findByEmailVerifiedFalse();
    }

    public UsernameValidationResult usernameValidation(String username) {
        if (username.isEmpty()) {
            return UsernameValidationResult.USERNAME_EMPTY;
        }
        if (findByUsername(username).isPresent()) {
            return UsernameValidationResult.USERNAME_DUPLICATE;
        }
        return UsernameValidationResult.VALIDATION_OK;
    }

    public EmailValidationResult emailValidation(String email) {
        if (email.isEmpty()) {
            return EmailValidationResult.EMAIL_EMPTY;
        }
        if (!isValidEmailForm(email)) {
            return EmailValidationResult.EMAIL_FORMAT_ERROR;
        }
        if (findByEmail(email).isPresent()) {
            return EmailValidationResult.EMAIL_DUPLICATE;
        }
        return EmailValidationResult.VALIDATION_OK;
    }

    private RsData<Member> oauthJoin(String providerTypeCode, String username, String name, String email) {
        if (findByUsername(username).isPresent()) {
            return RsData.of("F-1", "해당 아이디(%s)는 이미 사용중입니다.".formatted(username));
        }
        Optional<Member> opMember = memberRepository.findByEmail(email);
        if (opMember.isPresent()) {
            return RsData.of("F-2", "이미 가입된 이메일입니다.");
        }
        String encodedPassword = passwordEncoder.encode("");
        Member member = Member.of(providerTypeCode, username, name, email, encodedPassword, Role.BUYER);
        memberRepository.save(member);
        return RsData.of("S-1", "회원가입이 완료되었습니다.", member);
    }

    private boolean isValidEmailForm(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        Pattern pattern = Pattern.compile(emailRegex);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
}