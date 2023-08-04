package com.mypill.domain.member.service;

import com.mypill.domain.email.service.EmailVerificationService;
import com.mypill.domain.member.entity.Member;
import com.mypill.domain.member.exception.AlreadyJoinException;
import com.mypill.domain.member.repository.MemberRepository;
import com.mypill.domain.product.entity.Product;
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
    private final ApplicationEventPublisher publisher;

    @Transactional
    public RsData<Member> join(String username, String name, String password, String userTypeStr, String email, boolean emailVerified) {
        Integer userType = Integer.parseInt(userTypeStr);
        Member member = Member.builder()
                .username(username)
                .name(name)
                .password(passwordEncoder.encode(password))
                .email(email)
                .userType(userType)
                .emailVerified(emailVerified)
                .build();
        Member savedMember = memberRepository.save(member);
        return RsData.of("S-1", "회원가입이 완료되었습니다.", savedMember);
    }

    @Transactional
    public RsData<Member> join(String username, String name, String password, Integer userType, String email) {
        if (memberRepository.findByUsername(username).isPresent()) {
            throw new AlreadyJoinException("%s(은)는 이미 사용중인 아이디 입니다.".formatted(username));
        }
        Member member = Member.builder()
                .username(username)
                .name(name)
                .password(passwordEncoder.encode(password))
                .email(email)
                .userType(userType)
                .build();
        Member savedMember = memberRepository.save(member);
        sendEmail(savedMember);
        return RsData.of("S-1", "회원가입이 완료되었습니다.", savedMember);
    }

    private void sendEmail(Member member) {
        CompletableFuture<RsData<Long>> sendRsFuture = emailVerificationService.send(member);
        sendRsFuture.whenComplete((sendRs, throwable) -> {
            if (sendRs.isSuccess()) {
                log.info("이메일 인증 메일 발송 성공");
            } else {
                log.info("이메일 인증 메일 발송 실패");
            }
        });
    }

    @Transactional
    public RsData<Member> whenSocialLogin(String providerTypeCode, String username, String name, String email) {
        Optional<Member> opMember = findByUsername(username);
        return opMember.map(member -> RsData.of("S-2", "로그인 되었습니다.", member))
                .orElseGet(() -> oauthJoin(providerTypeCode, username, name, email));
    }

    private RsData<Member> oauthJoin(String providerTypeCode, String username, String name, String email) {
        if (findByUsername(username).isPresent()) {
            return RsData.of("F-1", "해당 아이디(%s)는 이미 사용중입니다.".formatted(username));
        }
        Optional<Member> opMember = memberRepository.findByEmail(email);
        if (opMember.isPresent()) {
            return RsData.of("F-2", "이미 가입된 이메일입니다.");
        }
        Member member = Member.builder()
                .providerTypeCode(providerTypeCode)
                .username(username)
                .name(name)
                .password(passwordEncoder.encode(""))
                .userType(1)
                .email(email)
                .build();
        memberRepository.save(member);
        return RsData.of("S-1", "회원가입이 완료되었습니다.", member);
    }

    public Optional<Member> findById(Long id) {
        return memberRepository.findById(id);
    }

    public Optional<Member> findByUsername(String username) {
        return memberRepository.findByUsername(username);
    }

    @Transactional
    public RsData<Member> updateName(Member member, String newName) {
        member = member.toBuilder()
                .name(newName)
                .build();
        memberRepository.save(member);
        return RsData.of("S-1", "이름 변경이 완료되었습니다.", member);
    }

    @Transactional
    public RsData<Member> deleteAccount(Member member) {
        if (member == null) {
            return RsData.of("F-1", "로그인이 필요한 서비스입니다.");
        }
        member.softDelete();
        Member deletedMember = memberRepository.save(member);
        publisher.publishEvent(new EventAfterDeleteMember(this, member));
        return RsData.of("S-1", "회원 탈퇴가 완료되었습니다.", deletedMember);
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
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        Pattern pattern = Pattern.compile(emailRegex);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
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
    public RsData verifyEmail(Long id, String verificationCode) {
        RsData verifyVerificationCodeRs = emailVerificationService.verifyVerificationCode(id, verificationCode);
        if (!verifyVerificationCodeRs.isSuccess()) {
            return verifyVerificationCodeRs;
        }
        Optional<Member> opMember = memberRepository.findById(id);
        if (opMember.isEmpty()) {
            return RsData.of("F-1", "이메일 인증에 실패했습니다.");
        }
        Member member = opMember.get();
        member = member.toBuilder()
                .emailVerified(true)
                .build();
        memberRepository.save(member);
        return RsData.of("S-1", "이메일인증이 완료되었습니다.");
    }

    public List<Member> getUnverifiedMember() {
        return memberRepository.findByEmailVerifiedFalse();
    }

    @Transactional
    public void deleteMember(Member member) {
        memberRepository.delete(member);
    }
}