package com.mypill.global.event;

import com.mypill.domain.member.entity.Member;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class EventAfterDeleteMember extends ApplicationEvent {
    private final Member member;

    public EventAfterDeleteMember(Object source, Member member) {
        super(source);
        this.member = member;
    }
}
