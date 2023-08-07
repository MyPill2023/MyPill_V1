package com.mypill.domain.member.controller;

import com.mypill.domain.member.dto.request.JoinRequest;
import com.mypill.domain.member.service.MemberService;
import com.mypill.domain.member.validation.EmailValidationResult;
import com.mypill.domain.member.validation.UsernameValidationResult;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.MethodName.class)
class MemberControllerTest {
    @Autowired
    private MockMvc mvc;
    @Autowired
    private MemberService memberService;

    @Test
    @DisplayName("로그인")
    @WithAnonymousUser
    void loginTest() throws Exception {
        // WHEN
        ResultActions resultActions = mvc
                .perform(get("/member/login")
                        .with(csrf())
                )
                .andDo(print());

        // THEN
        resultActions
                .andExpect(handler().handlerType(MemberController.class))
                .andExpect(handler().methodName("showLogin"))
                .andExpect(status().is2xxSuccessful())
        ;
    }

    @Test
    @DisplayName("회원가입 페이지 이동")
    @WithAnonymousUser
    void joinGetTest() throws Exception {
        // WHEN
        ResultActions resultActions = mvc
                .perform(get("/member/join")
                        .with(csrf())
                )
                .andDo(print());

        // THEN
        resultActions
                .andExpect(handler().handlerType(MemberController.class))
                .andExpect(handler().methodName("showJoin"))
                .andExpect(status().is2xxSuccessful())
        ;
    }

    @Test
    @DisplayName("회원가입")
    @WithAnonymousUser
    void joinPostTest() throws Exception {
        // GIVEN
        String username = "testUser1";
        String name = "김철수";
        String password = "abc12345";
        String email = "testEmail@test.com";
        String userType = "1";

        // WHEN
        ResultActions resultActions = mvc
                .perform(post("/member/join")
                        .with(csrf())
                        .param("username", username)
                        .param("name", name)
                        .param("password", password)
                        .param("email", email)
                        .param("userType", userType)
                )
                .andDo(print());

        // THEN
        resultActions
                .andExpect(handler().handlerType(MemberController.class))
                .andExpect(handler().methodName("join"))
                .andExpect(status().is3xxRedirection())
        ;
    }

    @Test
    @DisplayName("아이디 중복확인")
    void UsernameCheckTest() throws Exception {
        // GIVEN
        String username = "testUser1";

        // WHEN
        ResultActions resultActions = mvc
                .perform(get("/member/join/usernameCheck")
                        .with(csrf())
                        .param("username", username)
                )
                .andDo(print());

        // THEN
        resultActions
                .andExpect(handler().handlerType(MemberController.class))
                .andExpect(handler().methodName("usernameCheck"))
                .andExpect(content().json("{\"resultCode\":\"" + UsernameValidationResult.VALIDATION_OK.getResultCode()
                        + "\",\"msg\":\"" + UsernameValidationResult.VALIDATION_OK.getMessage()
                        + "\",\"data\":\"VALIDATION_OK\"}"))        ;
    }

    @Test
    @DisplayName("이메일 중복확인")
    void emailCheckTest() throws Exception {
        // GIVEN
        String email = "testEmail@test.com";

        // WHEN
        ResultActions resultActions = mvc
                .perform(get("/member/join/emailCheck")
                        .with(csrf())
                        .param("email", email)
                )
                .andDo(print());

        // THEN
        resultActions
                .andExpect(handler().handlerType(MemberController.class))
                .andExpect(handler().methodName("emailCheck"))
                .andExpect(content().json("{\"resultCode\":\"" + EmailValidationResult.VALIDATION_OK.getResultCode()
                        + "\",\"msg\":\"" + EmailValidationResult.VALIDATION_OK.getMessage()
                        + "\",\"data\":\"VALIDATION_OK\"}"))
        ;
    }

    @Test
    @WithMockUser(username = "testUser1", authorities = "MEMBER")
    @DisplayName("마이페이지 이동")
    void myPageTest1() throws Exception {
        // GIVEN
        String username = "testUser1";
        String name = "김철수";
        String password = "1234";
        String email = "testEmail@test.com";
        String userType = "판매자";
        memberService.join(new JoinRequest(username, name, password, email, userType));

        // WHEN
        ResultActions resultActions = mvc
                .perform(get("/member/myPage")
                        .with(csrf())
                )
                .andDo(print());

        // THEN
        resultActions
                .andExpect(handler().handlerType(MemberController.class))
                .andExpect(handler().methodName("showMyPage"))
                .andExpect(status().is2xxSuccessful())
        ;
    }

    @Test
    @WithMockUser(username = "testUser1", authorities = "MEMBER")
    @DisplayName("회원정보")
    void myInfoTest() throws Exception {
        // GIVEN
        String username = "testUser1";
        String name = "김철수";
        String password = "1234";
        String email = "testEmail@test.com";
        String userType = "판매자";
        memberService.join(new JoinRequest(username, name, password, email, userType));

        // WHEN
        ResultActions resultActions = mvc
                .perform(get("/member/myInfo")
                        .with(csrf())
                )
                .andDo(print());

        // THEN
        resultActions
                .andExpect(handler().handlerType(MemberController.class))
                .andExpect(handler().methodName("showMyInfo"))
                .andExpect(status().is2xxSuccessful())
        ;
    }

    @Test
    @WithMockUser(username = "testUser1", authorities = "MEMBER")
    @DisplayName("회원 탈퇴")
    void deleteAccountTest() throws Exception {
        // GIVEN
        String username = "testUser1";
        String name = "김철수";
        String password = "1234";
        String email = "testEmail@test.com";
        String userType = "판매자";
        memberService.join(new JoinRequest(username, name, password, email, userType));

        // WHEN
        ResultActions resultActions = mvc
                .perform(get("/member/deleteAccount")
                        .with(csrf())
                )
                .andDo(print());

        // THEN
        resultActions
                .andExpect(handler().handlerType(MemberController.class))
                .andExpect(handler().methodName("deleteAccount"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @WithMockUser(username = "testUser1", authorities = "MEMBER")
    @DisplayName("회원 탈퇴")
    void nameUpdate() throws Exception {
        // GIVEN
        String username = "testUser1";
        String name = "김철수";
        String password = "1234";
        String email = "testEmail@test.com";
        String userType = "판매자";
        memberService.join(new JoinRequest(username, name, password, email, userType));
        String newName = "손흥민";

        // WHEN
        ResultActions resultActions = mvc
                .perform(post("/member/name/update")
                        .with(csrf())
                        .param("newName", newName)
                )
                .andDo(print());

        // THEN
        resultActions
                .andExpect(handler().handlerType(MemberController.class))
                .andExpect(handler().methodName("updateName"))
                .andExpect(content().string(Matchers.matchesRegex(".*S-.*")))
        ;
    }
}