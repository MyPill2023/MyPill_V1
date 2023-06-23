package com.mypill.global.initData;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;

import com.mypill.domain.member.entity.Member;
import com.mypill.domain.member.service.MemberService;

@Configuration
public class NotProd {


    @Bean
    CommandLineRunner initData(
            MemberService memberService
    ) {
        return new CommandLineRunner() {
            @Override
            @Transactional
            public void run(String... args) {
                Member memberUser1 = memberService.join("user1", "1", "김철수", "1234", "cs@naver.com").getData();
                Member memberUser2 = memberService.join("user2", "1", "김영희", "1234", "yh@naver.com").getData();
                Member memberUser3 = memberService.join("user3", "2", "김짱구", "1234", "zzang@naver.com").getData();
                Member memberUser4 = memberService.join("user4", "2", "김맹구", "1234", "mk@naver.com").getData();
                Member memberUser5 = memberService.join("user5", "1", "김훈이", "1234", "hoon2@naver.com").getData();
            }
        };
    }
}
