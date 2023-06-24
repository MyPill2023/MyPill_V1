package com.mypill.domain.member.service;

import com.mypill.domain.member.entity.Member;
import com.mypill.domain.member.repository.MemberRepository;
import com.mypill.global.jwt.JwtProvider;
import com.mypill.global.rsData.RsData;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    @Transactional
    public RsData<Member> join(String userId, String username, String password, String userTypeStr, String email) {
        Integer userType = Integer.parseInt(userTypeStr);
        Member newMember = Member.builder()
                .userId(userId)
                .username(username)
                .password(passwordEncoder.encode(password))
                .email(email)
                .userType(userType)
                .build();
        Member savedMember = memberRepository.save(newMember);
        return RsData.of("S-1", "회원가입 되었습니다.", savedMember);
    }

    public Optional<Member> findByUserId(String userId) {
        return memberRepository.findByUserId(userId);
    }

    public String genAccessToken(String username, String password) {
        Member member = findByUserId(username).orElse(null);

        if (member == null) return null;

        if (!passwordEncoder.matches(password, member.getPassword())) {
            return null;
        }

        return jwtProvider.genToken(member.toClaims(), 60 * 60 * 24 * 365);
    }






    @Transactional
    public RsData login(String userId, String password) {
        if (userId.length() == 0) {
            return RsData.of("F-1", "아이디를 입력하세요.");
        }
        if (password.length() == 0) {
            return RsData.of("F-2", "비밀번호를 입력하세요.");
        }
        Member member = memberRepository.findByUserId(userId).orElse(null);
        if (member == null) {
            return RsData.of("F-3", "등록되지 않은 계정입니다.");
        }
        if (!passwordEncoder.matches(password, member.getPassword())) {
            return RsData.of("F-4", "비밀번호가 일치하지 않습니다.");
        }
        return RsData.of("F-5", "알 수 없는 에러가 발생했습니다.");
    }

    public boolean isIdDuplicated(String userId) {
        if (memberRepository.findByUserId(userId).isPresent()) {
            return true;
        }
        return false;
    }

    public int isValidEmail(String email) {
        if (email.equals("")) {
            return 1;
        }
        if (memberRepository.findByEmail(email).isPresent()) {
            return 3;
        }
        return 0;
    }
}
