package com.mypill.domain.diary.repository;

import com.mypill.domain.diary.entity.Diary;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


public interface DiaryRepository extends JpaRepository<Diary, Long> {

    List<Diary> findByDeleteDateIsNullOrderByCreateDateDesc();
    List<Diary> findByDeleteDateNull();
    Optional<Diary> findByDeleteDateNullAndId(long diaryId);

}
