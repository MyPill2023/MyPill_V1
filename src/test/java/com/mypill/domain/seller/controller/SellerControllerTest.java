package com.mypill.domain.seller.controller;

import com.mypill.domain.buyer.controller.BuyerController;
import com.mypill.domain.member.entity.Member;
import com.mypill.domain.member.repository.MemberRepository;
import com.mypill.domain.member.service.MemberService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
        Member testUser1  = Member.builder()
                .username("testUser1")
                .name("김철수")
                .password("1234")
                .userType(2)
                .email("testEmail@test.com")
                .build();
        memberRepository.save(testUser1);
    }

    @Test
    @WithMockUser(username = "testUser1", roles = "MEMBER")
    @DisplayName("판매자 마이페이지")
    void myPageTest() throws Exception {
        // WHEN
        ResultActions resultActions = mvc
                .perform(get("/usr/seller/myPage")
                        .with(csrf())
                )
                .andDo(print());

        // THEN
        resultActions
                .andExpect(handler().handlerType(SellerController.class))
                .andExpect(handler().methodName("myPage"))
                .andExpect(status().is2xxSuccessful())
        ;
    }


    @Test
    @WithMockUser(username = "testUser1", roles = "MEMBER")
    @DisplayName("판매자 회원정보")
    void myInfoTest() throws Exception {
        // WHEN
        ResultActions resultActions = mvc
                .perform(get("/usr/seller/myInfo")
                        .with(csrf())
                )
                .andDo(print());

        // THEN
        resultActions
                .andExpect(handler().handlerType(SellerController.class))
                .andExpect(handler().methodName("myInfo"))
                .andExpect(status().is2xxSuccessful())
        ;
    }


    @Test
    void orderManagement() {
    }

    @Test
    void certificate() {
    }

    @Test
    void brnoCertificate() {
    }

    @Test
    void nBrnoCertificate() {
    }
}