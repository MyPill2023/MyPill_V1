package com.mypill.domain.member.service;

import com.mypill.domain.member.entity.Member;
import com.mypill.domain.member.exception.AlreadyJoinException;
import com.mypill.domain.member.repository.MemberRepository;
import com.mypill.global.security.jwt.JwtProvider;
import com.mypill.global.rsData.RsData;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

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

        return RsData.of("S-1", "회원가입 되었습니다.", savedMember);
    }

    public Optional<Member> findByUsername(String username) {
        return memberRepository.findByUsername(username);
    }

    public Optional<Member> findById(Long id) {
        return memberRepository.findById(id);
    }

    // JWT 를 위한 user 객체 생성
    public void forceAuthentication(Member member) {
        User user = new User(member.getUsername(), "", member.getGrantedAuthorities());

        UsernamePasswordAuthenticationToken authentication =
                UsernamePasswordAuthenticationToken.authenticated(
                        user,
                        null,
                        member.getGrantedAuthorities()
                );

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);
    }

    @Transactional
    public String genAccessToken(Member member) {
        String accessToken = member.getAccessToken();

        if (!StringUtils.hasLength(accessToken)) {
            accessToken = jwtProvider.generateAccessToken(member.getAccessTokenClaims(), 60L * 60 * 24 * 365 * 100);
            member.setAccessToken(accessToken);
        }

        return accessToken;
    }

    public boolean verifyWithWhiteList(Member member, String token) {
        return member.getAccessToken().equals(token);
    }
}