package com.mypill.global.event;

import com.mypill.domain.member.entity.Member;
import com.mypill.domain.order.entity.Order;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class EventAfterOrderCanceled extends ApplicationEvent {

    private final Member seller;
    private final Order order;

    public EventAfterOrderCanceled(Object source, Member seller, Order order) {
        super(source);
        this.seller = seller;
        this.order = order;
    }
}
