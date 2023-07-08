package com.mypill.global.batch;

import com.mypill.domain.member.entity.Member;
import com.mypill.domain.member.service.MemberService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BatchJobScheduler {
    private final MemberService memberService;

    public BatchJobScheduler(MemberService memberService) {
        this.memberService = memberService;
    }

    @Scheduled(cron = "0 0 4 * * *")
    public void deleteUnverifiedMembers() {
        List<Member> unverifiedMembers = memberService.getUnverifiedMember();
        for (Member member : unverifiedMembers) {
            memberService.deleteMember(member);
        }
    }
}