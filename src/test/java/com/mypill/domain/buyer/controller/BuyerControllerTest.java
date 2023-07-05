package com.mypill.domain.buyer.controller;

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
    private MemberService memberService;
    @Autowired
    private MemberRepository memberRepository;

    @BeforeEach
    void beforeEach() {
        String username = "testUser1";
        String name = "김철수";
        String password = "1234";
        String email = "testEmail@test.com";
        Integer userType = 1;
        Member testUser1 = memberService.join(username, name, password, userType, email).getData();
        testUser1 = testUser1.toBuilder()
                .likedProducts(new ArrayList<>())
                .build();
        memberRepository.save(testUser1);
    }


    @Test
    @WithMockUser(username = "testUser1", roles = "MEMBER")
    @DisplayName("구매자 마이페이지")
    void myPageTest() throws Exception {
        // WHEN
        ResultActions resultActions = mvc
                .perform(get("/usr/buyer/myPage")
                        .with(csrf())
                )
                .andDo(print());

        // THEN
        resultActions
                .andExpect(handler().handlerType(BuyerController.class))
                .andExpect(handler().methodName("myPage"))
                .andExpect(status().is2xxSuccessful())
        ;
    }

    @Test
    @WithMockUser(username = "testUser1", roles = "MEMBER")
    @DisplayName("구매자 회원정보")
    void myInfoTest() throws Exception {
        // WHEN
        ResultActions resultActions = mvc
                .perform(get("/usr/buyer/myInfo")
                        .with(csrf())
                )
                .andDo(print());

        // THEN
        resultActions
                .andExpect(handler().handlerType(BuyerController.class))
                .andExpect(handler().methodName("myInfo"))
                .andExpect(status().is2xxSuccessful())
        ;
    }

    @Test
    @WithMockUser(username = "testUser1", roles = "MEMBER")
    @DisplayName("구매자 회원정보")
    void myLikesTest() throws Exception {
        // WHEN
        ResultActions resultActions = mvc
                .perform(get("/usr/buyer/myLikes")
                        .with(csrf())
                )
                .andDo(print());

        // THEN
        resultActions
                .andExpect(handler().handlerType(BuyerController.class))
                .andExpect(handler().methodName("myLikes"))
                .andExpect(status().is2xxSuccessful())
        ;
    }

    @Test
    @WithMockUser(username = "testUser1", roles = "MEMBER")
    @DisplayName("내 글 목록")
    void myPostsTest() throws Exception {
        // WHEN
        ResultActions resultActions = mvc
                .perform(get("/usr/buyer/myPosts")
                        .with(csrf())
                )
                .andDo(print());

        // THEN
        resultActions
                .andExpect(handler().handlerType(BuyerController.class))
                .andExpect(handler().methodName("myPosts"))
                .andExpect(status().is2xxSuccessful())
        ;
    }

    @Test
    @WithMockUser(username = "testUser1", roles = "MEMBER")
    @DisplayName("내 댓글 목록")
    void myCommentsTest() throws Exception {
        // WHEN
        ResultActions resultActions = mvc
                .perform(get("/usr/buyer/myComments")
                        .with(csrf())
                )
                .andDo(print());

        // THEN
        resultActions
                .andExpect(handler().handlerType(BuyerController.class))
                .andExpect(handler().methodName("myComments"))
                .andExpect(status().is2xxSuccessful())
        ;
    }

    @Test
    void mySchedule() {
    }

    @Test
    void myOrder() {
    }

    @Test
    void myAddress() {
    }
}