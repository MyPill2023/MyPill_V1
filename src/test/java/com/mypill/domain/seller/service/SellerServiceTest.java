package com.mypill.domain.seller.service;

import com.mypill.domain.member.entity.Member;
import com.mypill.domain.member.repository.MemberRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;


import static org.assertj.core.api.Assertions.assertThat;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.MethodName.class)
class SellerServiceTest {
    @Autowired
    private SellerService sellerService;
    @Autowired
    private MemberRepository memberRepository;
    private Member testUser1;

    @Test
    @WithMockUser(username = "testUser1", authorities = "WAITER")
    @DisplayName("통신판매업 검증")
    void certificateBRNOTest1() {
        // GIVEN
        testUser1 = Member.builder()
                .username("testUser1")
                .name("김철수")
                .password("1234")
                .userType(3)
                .email("testEmail@test.com")
                .brnoCertificated(false)
                .nBrnoCertificated(false)
                .build();
        memberRepository.save(testUser1);
        String brno = "7598700821";

        // WHEN
        sellerService.certificateBRNO(brno, testUser1);

        // THEN
        assertTrue(testUser1.isBrnoCertificated());

    }

    @Test
    @WithMockUser(username = "testUser1", authorities = "WAITER")
    @DisplayName("통신판매업 검증 후 판매자 자격 변경")
    void certificateBRNOTest2() {
        // GIVEN
        testUser1 = Member.builder()
                .username("testUser1")
                .name("김철수")
                .password("1234")
                .userType(3)
                .email("testEmail@test.com")
                .brnoCertificated(false)
                .nBrnoCertificated(true)
                .build();
        memberRepository.save(testUser1);
        String brno = "7598700821";

        // WHEN
        sellerService.certificateBRNO(brno, testUser1);

        // THEN
        assertTrue(testUser1.isBrnoCertificated());
        assertThat(testUser1.getUserType()).isEqualTo(2);
    }

    @Test
    @WithMockUser(username = "testUser1", authorities = "WAITER")
    @DisplayName("건강기능식품 판매업 검증")
    void certificateNBRNOTest1() {
        // GIVEN
        testUser1 = Member.builder()
                .username("testUser1")
                .name("김철수")
                .password("1234")
                .userType(3)
                .email("testEmail@test.com")
                .brnoCertificated(false)
                .nBrnoCertificated(false)
                .build();
        memberRepository.save(testUser1);
        String nBrno = "20180107318";

        // WHEN
        sellerService.certificateNBRNO(nBrno, testUser1);

        // THEN
        assertTrue(testUser1.isNBrnoCertificated());
    }

    @Test
    @WithMockUser(username = "testUser1", authorities = "WAITER")
    @DisplayName("건강기능식품 판매업 검증 후 판매자 자격 변경")
    void certificateNBRNOTest2() {
        // GIVEN
        testUser1 = Member.builder()
                .username("testUser1")
                .name("김철수")
                .password("1234")
                .userType(3)
                .email("testEmail@test.com")
                .brnoCertificated(true)
                .nBrnoCertificated(false)
                .build();
        memberRepository.save(testUser1);
        String nBrno = "20180107318";

        // WHEN
        sellerService.certificateNBRNO(nBrno, testUser1);

        // THEN
        assertTrue(testUser1.isNBrnoCertificated());
        assertThat(testUser1.getUserType()).isEqualTo(2);
    }
}