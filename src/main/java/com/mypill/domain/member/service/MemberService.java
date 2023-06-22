package com.mypill.domain.member.service;

import com.mypill.domain.member.entity.Member;
import com.mypill.domain.member.repository.MemberRepository;
import com.mypill.global.rsData.RsData;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;


    public RsData<Member> login(String userId, String password) {
        Member member = memberRepository.findByUserId(userId).orElse(null);
        if (member == null) {
            return RsData.of("F-1", "등록되지 않은 계정입니다.");
        }
        if (!passwordEncoder.matches(password, member.getPassword())) {
            return RsData.of("F-2", "비밀번호가 일치하지 않습니다.");
        }
        return RsData.of("S-1", "로그인되었습니다.", member);
    }

    public RsData<Member> join(String userId, String username, String password, String email) {
        Member newMember = Member.builder()
                .userId(userId)
                .username(username)
                .password(password)
                .email(email)
                .build();
        Member savedMember = memberRepository.save(newMember);
        return RsData.of("S-1", "회원가입 되었습니다.", savedMember);
    }
}
