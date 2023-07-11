package com.mypill.domain.seller.controller;

import com.mypill.domain.member.entity.Member;
import com.mypill.domain.member.repository.MemberRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.handler;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.MethodName.class)
class SellerControllerTest {
    @Autowired
    private MockMvc mvc;
    @Autowired
    private MemberRepository memberRepository;

    @BeforeEach
    void beforeEach() {
        Member testUser1 = Member.builder()
                .username("testUser1")
                .name("김철수")
                .password("1234")
                .userType(3)
                .email("testEmail@test.com")
                .build();
        memberRepository.save(testUser1);
    }

    @Test
    @WithMockUser(username = "testUser1", authorities = "WAITER")
    @DisplayName("판매자 인증 페이지 이동")
    void certificateTest() throws Exception {
        // WHEN
        ResultActions resultActions = mvc
                .perform(get("/seller/certificate"))
                .andDo(print());

        // THEN
        resultActions
                .andExpect(handler().handlerType(SellerController.class))
                .andExpect(handler().methodName("certificate"))
                .andExpect(status().is2xxSuccessful())
        ;
    }

    @Test
    @WithMockUser(username = "testUser1", authorities = "WAITER")
    @DisplayName("통신판매업 인증")
    void brnoCertificateTest() throws Exception {
        // GIVEN
        String businessNumber = "7598700821";

        // WHEN
        ResultActions resultActions = mvc
                .perform(post("/seller/brnoCertificate")
                        .with(csrf())
                        .param("businessNumber", businessNumber)
                )
                .andDo(print());

        // THEN
        resultActions
                .andExpect(handler().handlerType(SellerController.class))
                .andExpect(handler().methodName("brnoCertificate"))
                .andExpect(status().is3xxRedirection())
        ;
    }

    @Test
    @WithMockUser(username = "testUser1", authorities = "WAITER")
    @DisplayName("건강기능식품 판매업 인증")
    void nBrnoCertificateTest() throws Exception {
        // GIVEN
        String nutrientBusinessNumber = "20180107318";

        // WHEN
        ResultActions resultActions = mvc
                .perform(post("/seller/nBrnoCertificate")
                        .with(csrf())
                        .param("nutrientBusinessNumber", nutrientBusinessNumber)
                )
                .andDo(print());

        // THEN
        resultActions
                .andExpect(handler().handlerType(SellerController.class))
                .andExpect(handler().methodName("nBrnoCertificate"))
                .andExpect(status().is3xxRedirection())
        ;
    }
}