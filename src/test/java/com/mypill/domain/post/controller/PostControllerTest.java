package com.mypill.domain.post.controller;

import com.mypill.domain.member.dto.request.JoinRequest;
import com.mypill.domain.member.entity.Member;
import com.mypill.domain.member.service.MemberService;
import com.mypill.domain.post.dto.request.PostRequest;
import com.mypill.domain.post.entity.Post;
import com.mypill.domain.post.service.PostService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.handler;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.MethodName.class)
class PostControllerTest {
    @Autowired
    private MockMvc mvc;
    @Autowired
    private MemberService memberService;
    @Autowired
    private PostService postService;
    private Member testUser1;
    private MockMultipartFile emptyFile;

    @BeforeEach
    void beforeEach() {
        emptyFile = new MockMultipartFile("imageFile", new byte[0]);
        testUser1 = memberService.join(new JoinRequest("testUser1", "김철수", "1234", "test1@test.com", "구매자")).getData();
    }

    @Test
    @DisplayName("게시글 목록 컨트롤러 테스트")
    void showListTest() throws Exception {
        // GIVEN
        String keyword = "키워드";
        String searchType = "title";
        int pageNumber = 0;
        int pageSize = 10;

        // WHEN
        ResultActions resultActions = mvc
                .perform(get("/post/list")
                        .with(csrf())
                        .param("keyword", keyword)
                        .param("searchType", searchType)
                        .param("pageNumber", String.valueOf(pageNumber))
                        .param("pageSize", String.valueOf(pageSize))
                )
                .andDo(print());

        // THEN
        resultActions
                .andExpect(handler().handlerType(PostController.class))
                .andExpect(handler().methodName("showList"))
                .andExpect(status().is2xxSuccessful())
        ;
    }

    @Test
    @WithMockUser(username = "testUser1", authorities = "MEMBER")
    @DisplayName("게시글 등록 페이지 이동 컨트롤러 테스트")
    void createGetTest() throws Exception {
        // WHEN
        ResultActions resultActions = mvc
                .perform(get("/post/create")
                        .with(csrf())
                )
                .andDo(print());

        // THEN
        resultActions
                .andExpect(handler().handlerType(PostController.class))
                .andExpect(handler().methodName("showCreateForm"))
                .andExpect(status().is2xxSuccessful())
        ;
    }

    @Test
    @WithMockUser(username = "testUser1", authorities = "MEMBER")
    @DisplayName("게시글 등록 컨트롤러 테스트")
    void createPostTest() throws Exception {
        // GIVEN
        String title = "title";
        String content = "content";

        // WHEN
        ResultActions resultActions = mvc.perform(
                multipart("/post/create")
                        .file(emptyFile)
                        .param("title", title)
                        .param("content", content)
                        .with(csrf())
        ).andDo(print());

        // THEN
        resultActions
                .andExpect(handler().handlerType(PostController.class))
                .andExpect(handler().methodName("create"))
                .andExpect(status().is3xxRedirection())
        ;
    }

    @Test
    @WithMockUser(username = "testUser1", authorities = "MEMBER")
    @DisplayName("게시글 상세 컨트롤러 테스트")
    void showPostTest() throws Exception {
        // GIVEN
        PostRequest postRequest = new PostRequest();
        postRequest.setTitle("title");
        postRequest.setContent("content");
        postRequest.setImageFile(emptyFile);
        Post post = postService.create(postRequest, testUser1);

        // WHEN
        ResultActions resultActions = mvc
                .perform(get("/post/detail/" + post.getId())
                        .with(csrf())
                )
                .andDo(print());

        // THEN
        resultActions
                .andExpect(handler().handlerType(PostController.class))
                .andExpect(handler().methodName("showPost"))
                .andExpect(status().is2xxSuccessful())
        ;
    }

    @Test
    @WithMockUser(username = "testUser1", authorities = "MEMBER")
    @DisplayName("게시글 수정 폼 컨트롤러 테스트")
    void updateGetTest() throws Exception {
        // GIVEN
        PostRequest postRequest = new PostRequest();
        postRequest.setTitle("title");
        postRequest.setContent("content");
        postRequest.setImageFile(emptyFile);
        Post post = postService.create(postRequest, testUser1);

        // WHEN
        ResultActions resultActions = mvc
                .perform(get("/post/update/" + post.getId())
                        .with(csrf())
                )
                .andDo(print());

        // THEN
        resultActions
                .andExpect(handler().handlerType(PostController.class))
                .andExpect(handler().methodName("showUpdateForm"))
                .andExpect(status().is2xxSuccessful())
        ;
    }

    @Test
    @WithMockUser(username = "testUser1", authorities = "MEMBER")
    @DisplayName("게시글 수정 컨트롤러 테스트")
    void updatePostTest() throws Exception {
        // GIVEN
        PostRequest postRequest = new PostRequest();
        postRequest.setTitle("title");
        postRequest.setContent("content");
        postRequest.setImageFile(emptyFile);
        Post post = postService.create(postRequest, testUser1);

        // WHEN
        ResultActions resultActions = mvc.perform(
                multipart("/post/update/" + post.getId())
                        .file(emptyFile)
                        .param("title", post.getTitle())
                        .param("content", post.getContent())
                        .with(csrf())
        ).andDo(print());

        // THEN
        resultActions
                .andExpect(handler().handlerType(PostController.class))
                .andExpect(handler().methodName("update"))
                .andExpect(status().is3xxRedirection())
        ;
    }

    @Test
    @WithMockUser(username = "testUser1", authorities = "MEMBER")
    @DisplayName("게시글 삭제 컨트롤러 테스트")
    void delete() throws Exception {
        // GIVEN
        PostRequest postRequest = new PostRequest();
        postRequest.setTitle("title");
        postRequest.setContent("content");
        postRequest.setImageFile(emptyFile);
        Post post = postService.create(postRequest, testUser1);

        // WHEN
        ResultActions resultActions = mvc
                .perform(post("/post/delete/" + post.getId())
                        .with(csrf())
                )
                .andDo(print());

        // THEN
        resultActions
                .andExpect(handler().handlerType(PostController.class))
                .andExpect(handler().methodName("delete"))
                .andExpect(status().is3xxRedirection())
        ;
    }
}