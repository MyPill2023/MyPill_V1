package com.mypill.domain.diary.repository;

import com.mypill.domain.diary.entity.Diary;
import com.mypill.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface DiaryRepository extends JpaRepository<Diary, Long> {

    List<Diary> findByDeleteDateIsNullOrderByCreateDateDesc();

    List<Diary> findByMember (Member member);
}
