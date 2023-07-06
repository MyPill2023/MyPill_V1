package com.mypill.domain.diary.repository;

import com.mypill.domain.diary.entity.Diary;
import com.mypill.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;


public interface DiaryRepository extends JpaRepository<Diary, Long> {

    List<Diary> findByDeleteDateIsNullOrderByCreateDateDesc();

    List<Diary> findByMember (Member member);

    Optional<Diary> findByName (String name);

    List<Diary> findByDeleteDateNull();

    Optional<Diary> findByDeleteDateNullAndId(long diaryId);

    List<Diary> findByDeleteDate(LocalDate localDate);

}
