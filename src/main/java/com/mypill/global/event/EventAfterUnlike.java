package com.mypill.global.event;

import com.mypill.domain.member.entity.Member;
import com.mypill.domain.product.entity.Product;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class EventAfterUnlike extends ApplicationEvent {
    private final Member member;
    private final Product product;

    public EventAfterUnlike(Object source, Member member, Product product) {
        super(source);
        this.member = member;
        this.product = product;
    }
}
