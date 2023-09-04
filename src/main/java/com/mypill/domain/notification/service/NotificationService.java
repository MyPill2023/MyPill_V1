package com.mypill.domain.notification.service;

import com.mypill.domain.diary.entity.Diary;
import com.mypill.domain.member.entity.Member;
import com.mypill.domain.notification.entity.Notification;
import com.mypill.domain.notification.entity.NotificationTypeCode;
import com.mypill.domain.notification.repository.NotificationRepository;
import com.mypill.domain.order.entity.Order;
import com.mypill.domain.order.entity.OrderItem;
import com.mypill.domain.order.entity.OrderStatus;
import com.mypill.global.rsdata.RsData;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
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
    public void whenAfterOrderPayment(Member seller, Order order) {
        Notification notification = Notification.builder()
                .typeCode(NotificationTypeCode.OrderPayment)
                .member(seller)
                .order(order)
                .build();
        notificationRepository.save(notification);
    }

    @Transactional
    public void whenAfterOrderChanged(Member seller, Order order, OrderStatus orderStatus) {
        NotificationTypeCode notificationTypeCode = NotificationTypeCode.OrderPayment;
        if (orderStatus.equals(OrderStatus.CANCELED)) {
            notificationTypeCode = NotificationTypeCode.OrderCanceled;
        }
        Notification notification = Notification.builder()
                .typeCode(notificationTypeCode)
                .member(seller)
                .order(order)
                .build();
        notificationRepository.save(notification);
    }

    @Transactional
    public void whenAfterOrderCanceled(Member seller, Order order) {
        Notification notification = Notification.builder()
                .typeCode(NotificationTypeCode.OrderCanceled)
                .member(seller)
                .order(order)
                .build();
        notificationRepository.save(notification);
    }

    @Transactional
    public void whenBeforeDiaryCheck(Diary diary) {
        Notification notification = Notification.builder()
                .typeCode(NotificationTypeCode.Record)
                .member(diary.getMember())
                .diaryName(diary.getName())
                .diaryTime(diary.getTime())
                .build();
        notificationRepository.save(notification);
    }

    @Transactional
    public RsData<Notification> markAsRead(Member actor, Long notificationId) {
        Notification notification = findById(notificationId).orElse(null);
        if (notification == null) {
            return RsData.of("F-1", "존재하지 않는 알림입니다.");
        }
        if (!Objects.equals(notification.getMember().getId(), actor.getId())) {
            return RsData.of("F-2", "권한이 없습니다.");
        }
        notification.markAsRead();
        return RsData.of("S-1", "", notification);
    }

    public List<Notification> findByMemberId(Long memberId) {
        return notificationRepository.findByMemberIdOrderByCreateDateDesc(memberId);
    }

    public Optional<Notification> findById(Long id) {
        return notificationRepository.findById(id);
    }

    public boolean countUnreadNotificationsByMember(Member member) {
        return notificationRepository.countByMemberAndReadDateIsNull(member) > 0;
    }

    @Transactional
    public void hardDelete(LocalDateTime cutoffDate) {
        notificationRepository.hardDelete(cutoffDate);
    }
}
