package com.mypill.domain.diary.repository;

import com.mypill.domain.diary.entity.DiaryCheckLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface DiaryCheckLogRepository extends JpaRepository<DiaryCheckLog,Long> {
    Optional<DiaryCheckLog> findByDeleteDateNullAndId(long diaryId);
    List<DiaryCheckLog> findByCheckDate (LocalDate date);
}
