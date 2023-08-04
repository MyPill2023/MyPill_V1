package com.mypill.domain.diary.repository;

import com.mypill.domain.diary.entity.Diary;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;


public interface DiaryRepository extends JpaRepository<Diary, Long> {

    List<Diary> findByMemberIdAndDeleteDateIsNullOrderByTimeAsc(Long memberId);
    Optional<Diary> findByDeleteDateNullAndId(Long diaryId);
    List<Diary> findByDeleteDateNullAndTime(LocalTime time);

}
