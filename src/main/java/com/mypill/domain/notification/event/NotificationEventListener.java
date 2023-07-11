package com.mypill.domain.notification.event;

import com.mypill.domain.notification.service.NotificationService;
import com.mypill.global.event.EventAfterOrderCanceled;
import com.mypill.global.event.EventAfterOrderPayment;
import com.mypill.global.event.EventAfterOrderStatusUpdate;
import com.mypill.global.event.EventBeforeDiaryCheck;
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
    @EventListener
    public void listen(EventAfterOrderCanceled event) {
        notificationService.whenAfterOrderCanceled(event.getSeller(), event.getOrder());
    }

    @EventListener
    public void listen(EventBeforeDiaryCheck event) {
        notificationService.whenBeforeDiaryCheck(event.getDiary());
    }
}
