package com.mypill.domain.member.event;

import com.mypill.domain.member.service.MemberService;
import com.mypill.global.event.EventAfterLike;
import com.mypill.global.event.EventAfterUnlike;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
@RequiredArgsConstructor
public class MemberEventListener {
    private final MemberService memberService;

    @EventListener
    public void listen(EventAfterLike event) {
        memberService.whenAfterLike(event.getMember(), event.getProduct());
    }

    @EventListener
    public void listen(EventAfterUnlike event) {
        memberService.whenAfterUnlike(event.getMember(), event.getProduct());
    }
}
