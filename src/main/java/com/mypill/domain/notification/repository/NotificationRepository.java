package com.mypill.domain.notification.repository;

import com.mypill.domain.member.entity.Member;
import com.mypill.domain.notification.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByMemberIdOrderByCreateDateDesc(Long memberId);

    int countByMemberAndReadDateIsNull(Member member);
}
