package com.mypill.domain.buyer.controller;

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

import java.util.ArrayList;
import java.util.HashSet;

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
class BuyerControllerTest {
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
                .userType(1)
                .email("testEmail@test.com")
                .likedProducts(new HashSet<>())
                .build();
        memberRepository.save(testUser1);
    }


    @Test
    @WithMockUser(username = "testUser1", authorities = "BUYER")
    @DisplayName("구매자 좋아요 정보")
    void myLikesTest() throws Exception {
        // WHEN
        ResultActions resultActions = mvc
                .perform(get("/buyer/myLikes"))
                .andDo(print());

        // THEN
        resultActions
                .andExpect(handler().handlerType(BuyerController.class))
                .andExpect(handler().methodName("myLikes"))
                .andExpect(status().is2xxSuccessful())
        ;
    }

}