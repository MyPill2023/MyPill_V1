package com.mypill.global.event;

import com.mypill.domain.member.entity.Member;
import com.mypill.domain.order.entity.Order;
import com.mypill.domain.order.entity.OrderStatus;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class EventAfterOrderChanged extends ApplicationEvent {
    private final Member seller;
    private final Order order;
    private final OrderStatus orderStatus;

    public EventAfterOrderChanged(Object source, Member seller, Order order, OrderStatus orderStatus) {
        super(source);
        this.seller = seller;
        this.order = order;
        this.orderStatus = orderStatus;
    }
}
