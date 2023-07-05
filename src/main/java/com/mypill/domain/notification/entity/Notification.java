package com.mypill.domain.notification.entity;

import com.mypill.domain.member.entity.Member;
import com.mypill.domain.order.entity.Order;
import com.mypill.domain.order.entity.OrderItem;
import com.mypill.domain.order.entity.OrderStatus;
import com.mypill.global.base.entitiy.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Entity
@Getter
@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Notification extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    @Enumerated
    @Column(nullable = false)
    private NotificationTypeCode typeCode;

    @Column
    private LocalDateTime readDate;

    @ManyToOne(fetch = FetchType.LAZY)
    private OrderItem orderItem;

    private OrderStatus newStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    private Order order;

    public void markAsRead() {
        readDate = LocalDateTime.now();
    }

}
