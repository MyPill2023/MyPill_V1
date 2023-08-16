package com.mypill.domain.home.controller;

import com.mypill.domain.member.dto.request.JoinRequest;
import com.mypill.domain.member.service.MemberService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

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
class HomeControllerTest {
    @Autowired
    private MockMvc mvc;
    @Autowired
    private MemberService memberService;

    @BeforeEach
    void beforeEach() {
    }

    @Test
    @WithAnonymousUser
    @DisplayName("메인페이지 이동 - 비로그인")
    void showMainTest1() throws Exception {
        // WHEN
        ResultActions resultActions = mvc
                .perform(get("/home")
                        .with(csrf())
                )
                .andDo(print());

        // THEN
        resultActions
                .andExpect(handler().handlerType(HomeController.class))
                .andExpect(handler().methodName("showMain"))
                .andExpect(status().is2xxSuccessful())
        ;
    }

    @Test
    @WithMockUser(username = "testUser1", authorities = "MEMBER")
    @DisplayName("메인페이지 이동 - 로그인")
    void showMainTest2() throws Exception {
        // GIVEN
        memberService.join(new JoinRequest("testUser1", "김철수", "1234", "test1@test.com", "판매자"));

        // WHEN
        ResultActions resultActions = mvc
                .perform(get("/home")
                        .with(csrf())
                )
                .andDo(print());

        // THEN
        resultActions
                .andExpect(handler().handlerType(HomeController.class))
                .andExpect(handler().methodName("showMain"))
                .andExpect(status().is2xxSuccessful())
        ;
    }
}