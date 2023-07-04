package com.mypill.domain.diary.repository;

import com.mypill.domain.diary.entity.DiaryCheckLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

public interface DiaryCheckLogRepository extends JpaRepository<DiaryCheckLog,Long> {
    Optional<DiaryCheckLog> findByMemberAndCreateDate (Long memberId, LocalDate today);

    Optional<DiaryCheckLog> findByMemberAndCreateDateBetween (Long memberId, LocalDateTime startOfDay, LocalDateTime endOfDay);
}
