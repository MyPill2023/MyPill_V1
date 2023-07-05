package com.mypill.global.event;

import com.mypill.domain.member.entity.Member;
import com.mypill.domain.order.entity.OrderItem;
import com.mypill.domain.order.entity.OrderStatus;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class EventAfterOrderStatusUpdate extends ApplicationEvent {

    private final Member member;
    private final OrderItem orderItem;
    private final OrderStatus orderStatus;

    public EventAfterOrderStatusUpdate(Object source, Member member, OrderItem orderItem, OrderStatus orderStatus) {
        super(source);
        this.member = member;
        this.orderItem = orderItem;
        this.orderStatus = orderStatus;
    }
}
