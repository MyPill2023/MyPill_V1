package com.mypill.domain.notification.eventlistner;

import com.mypill.domain.notification.service.NotificationService;
import com.mypill.global.event.EventAfterOrderPayment;
import com.mypill.global.event.EventAfterOrderStatusUpdate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Transactional
@Slf4j
public class NotificationEventListener {

    private final NotificationService notificationService;

    @EventListener
    public void listen(EventAfterOrderStatusUpdate event) {
        notificationService.whenAfterOrderStatusUpdate(event.getMember(), event.getOrderItem(), event.getOrderStatus());
    }

    @EventListener
    public void listen(EventAfterOrderPayment event) {
        notificationService.whenAfterOrderPayment(event.getSeller(), event.getOrder());
    }
}
