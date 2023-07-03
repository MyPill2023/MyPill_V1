package com.mypill.global.security.service;

import com.mypill.domain.member.entity.Member;
import com.mypill.domain.member.repository.MemberRepository;
import com.mypill.global.security.exception.EmailVerificationException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException, EmailVerificationException {
        Member member = memberRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException(username));
        if (!member.getEmailVerified()) {
            throw new EmailVerificationException("이메일 인증이 완료되지 않은 계정입니다.");
        }
        return new User(member.getUsername(), member.getPassword(), member.getGrantedAuthorities());
    }
}