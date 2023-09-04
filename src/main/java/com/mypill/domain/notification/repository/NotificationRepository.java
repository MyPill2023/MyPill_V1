package com.mypill.domain.notification.repository;

import com.mypill.domain.member.entity.Member;
import com.mypill.domain.notification.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByMemberIdOrderByCreateDateDesc(Long memberId);

    int countByMemberAndReadDateIsNull(Member member);

    @Modifying
    @Query("delete from Notification n where n.createDate <= :cutoffDate")
    void hardDelete(@Param("cutoffDate") LocalDateTime cutoffDate);
}
