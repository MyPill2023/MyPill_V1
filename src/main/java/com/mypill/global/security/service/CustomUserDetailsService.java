package com.mypill.global.security.service;

import com.mypill.domain.member.entity.Member;
import com.mypill.domain.member.repository.MemberRepository;
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
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException{
        Member member = memberRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException(username));
        if (member.getDeleteDate() != null) {
            throw new UsernameNotFoundException("아이디 또는 비밀번호가 일치하지 않습니다.");
        }
        return new User(member.getUsername(), member.getPassword(), member.getGrantedAuthorities());
    }
}