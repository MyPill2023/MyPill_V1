package com.mypill.domain.notification.service;

import com.mypill.domain.member.entity.Member;
import com.mypill.domain.notification.entity.Notification;
import com.mypill.domain.notification.entity.NotificationTypeCode;
import com.mypill.domain.notification.repository.NotificationRepository;
import com.mypill.domain.order.entity.OrderItem;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public void whenAfterOrderStatusUpdate(Member member, OrderItem orderItem) {
        Notification notification = Notification.builder()
                .typeCode(NotificationTypeCode.OrderStatus)
                .member(member)
                .orderItem(orderItem)
                .build();

        notificationRepository.save(notification);
    }
}
