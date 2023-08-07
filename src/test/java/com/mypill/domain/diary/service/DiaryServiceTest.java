package com.mypill.domain.diary.service;


import com.mypill.domain.diary.entity.Diary;
import com.mypill.domain.diary.entity.DiaryCheckLog;
import com.mypill.domain.diary.repository.DiaryCheckLogRepository;
import com.mypill.domain.diary.repository.DiaryRepository;
import com.mypill.domain.member.dto.request.JoinRequest;
import com.mypill.domain.member.entity.Member;
import com.mypill.domain.member.service.MemberService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
@Transactional
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.MethodName.class)
class DiaryServiceTest {

    @Autowired
    private MemberService memberService;
    @Autowired
    private DiaryRepository diaryRepository;
    @Autowired
    private DiaryService diaryService;
    @Autowired
    private DiaryCheckLogRepository diaryCheckLogRepository;

    private Member testUser1;

    @BeforeEach
    void beforeEachTest() {
        testUser1 = memberService.join(new JoinRequest("testUserSeller1", "김철수", "1234", "testSeller1@test.com", "판매자")).getData();
        Diary diary = Diary.builder()
                .member(testUser1)
                .name("루테인")
                .time(LocalTime.now())
                .build();
        Diary savedDiary = diaryRepository.save(diary);

        DiaryCheckLog diaryCheckLog = DiaryCheckLog.builder()
                .diary(savedDiary)
                .member(testUser1)
                .checkDate(LocalDate.now())
                .build();
        diaryCheckLogRepository.save(diaryCheckLog);
    }

    @Test
    @DisplayName("체크한 기록 불러오기")
    void findHistoryTests() {
        // WHEN
        List<DiaryCheckLog> history = diaryService.findHistory(testUser1.getId());

        // THEN
        assertThat(history.size()).isEqualTo(1);
    }
}
