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
    private final String brno = "BUSINESS_NUMBER";
    private final String nBrno = "NUTRIENT_BUSINESS_NUMBER";

    @Test
    @WithMockUser(username = "testUser1", authorities = "WAITER")
    @DisplayName("통신판매업 검증 실패")
    void certificateBRNOFailTest1() {
        // GIVEN
        testUser1 = Member.builder()
                .username("testUser1")
                .name("김철수")
                .password("1234")
                .userType(3)
                .email("testEmail@test.com")
                .build();
        memberRepository.save(testUser1);

        // WHEN
        sellerService.businessNumberCheck(brno, testUser1);

        // THEN
        assertThat(testUser1.getBusinessNumber()).isNull();
    }

    @Test
    @WithMockUser(username = "testUser1", authorities = "WAITER")
    @DisplayName("통신판매업 검증 실패 후 대기자 자격 그대로")
    void certificateBRNOFailTest2() {
        // GIVEN
        testUser1 = Member.builder()
                .username("testUser1")
                .name("김철수")
                .password("1234")
                .userType(3)
                .email("testEmail@test.com")
                .nutrientBusinessNumber(nBrno)
                .build();
        memberRepository.save(testUser1);

        // WHEN
        sellerService.businessNumberCheck(brno, testUser1);

        // THEN
        assertThat(testUser1.getBusinessNumber()).isNull();
        assertThat(testUser1.getUserType()).isEqualTo(3);
    }

    @Test
    @WithMockUser(username = "testUser1", authorities = "WAITER")
    @DisplayName("건강기능식품 판매업 검증 실패")
    void certificateNBRNOFailTest1() {
        // GIVEN
        testUser1 = Member.builder()
                .username("testUser1")
                .name("김철수")
                .password("1234")
                .userType(3)
                .email("testEmail@test.com")
                .build();
        memberRepository.save(testUser1);

        // WHEN
        sellerService.nutrientBusinessNumberCheck(nBrno, testUser1);

        // THEN
        assertThat(testUser1.getNutrientBusinessNumber()).isNull();
    }

    @Test
    @WithMockUser(username = "testUser1", authorities = "WAITER")
    @DisplayName("건강기능식품 판매업 검증 실패 후 대기자 자격 그대로")
    void certificateNBRNOFailTest2() {
        // GIVEN
        testUser1 = Member.builder()
                .username("testUser1")
                .name("김철수")
                .password("1234")
                .userType(3)
                .email("testEmail@test.com")
                .businessNumber(brno)
                .build();
        memberRepository.save(testUser1);

        // WHEN
        sellerService.nutrientBusinessNumberCheck(nBrno, testUser1);

        // THEN
        assertThat(testUser1.getNutrientBusinessNumber()).isNull();
        assertThat(testUser1.getUserType()).isEqualTo(3);
    }
}