//package com.mypill.global.scheduler;
//
//import com.mypill.domain.member.entity.Member;
//import com.mypill.domain.member.repository.MemberRepository;
//import org.junit.jupiter.api.MethodOrderer;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.TestMethodOrder;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.ActiveProfiles;
//import org.springframework.transaction.annotation.Transactional;
//
//import static org.assertj.core.api.Assertions.assertThat;
//
//@SpringBootTest
//@Transactional
//@ActiveProfiles("test")
//@TestMethodOrder(MethodOrderer.MethodName.class)
//class SchedulerTest {
//
//    @Autowired
//    private Scheduler scheduler;
//    @Autowired
//    private MemberRepository memberRepository;
//
//    @Test
//    void deleteUnverifiedMembersTest() {
//        // GIVEN
//        Member member1 = Member.builder()
//                .username("testUser1")
//                .name("name1")
//                .password("password1")
//                .userType(1)
//                .email("test1@test.com")
//                .emailVerified(false)
//                .build();
//        Member member2 = Member.builder()
//                .username("testUser2")
//                .name("name2")
//                .password("password2")
//                .userType(2)
//                .email("test2@test.com")
//                .emailVerified(false)
//                .build();
//        Member member3 = Member.builder()
//                .username("testUser3")
//                .name("name3")
//                .password("password3")
//                .userType(3)
//                .email("test3@test.com")
//                .emailVerified(true)
//                .build();
//        memberRepository.save(member1);
//        memberRepository.save(member2);
//        memberRepository.save(member3);
//
//        // WHEN
//        scheduler.deleteUnverifiedMembers();
//
//        // THEN
//        assertThat(memberRepository.count()).isEqualTo(1);
//        assertThat(memberRepository.findAll().get(0).getUsername()).isEqualTo("testUser3");
//    }
//}
