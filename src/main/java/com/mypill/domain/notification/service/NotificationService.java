package com.mypill.domain.notification.service;

import com.mypill.domain.member.entity.Member;
import com.mypill.domain.notification.entity.Notification;
import com.mypill.domain.notification.entity.NotificationTypeCode;
import com.mypill.domain.notification.repository.NotificationRepository;
import com.mypill.domain.order.entity.OrderItem;
import com.mypill.domain.order.entity.OrderStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;

    @Transactional
    public void whenAfterOrderStatusUpdate(Member member, OrderItem orderItem, OrderStatus newStatus) {

        Notification notification = Notification.builder()
                .typeCode(NotificationTypeCode.OrderStatus)
                .member(member)
                .orderItem(orderItem)
                .newStatus(newStatus)
                .build();

        notificationRepository.save(notification);
    }

    @Transactional
    public void makeAsRead(Long notificationId) {
        Notification notification = findById(notificationId).orElseThrow();
        notification.markAsRead();
    }

    public List<Notification> findByMemberId(Long memberId){
        return notificationRepository.findByMemberIdOrderByCreateDateDesc(memberId);
    }
    public Optional<Notification> findById(Long id){
        return notificationRepository.findById(id);
    }
}
