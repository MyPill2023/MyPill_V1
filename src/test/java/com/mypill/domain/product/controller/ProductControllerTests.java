package com.mypill.domain.product.controller;


import com.mypill.domain.member.entity.Member;
import com.mypill.domain.member.service.MemberService;
import com.mypill.domain.product.dto.request.ProductRequest;
import com.mypill.domain.product.service.ProductService;
import com.mypill.domain.product.entity.Product;
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

import static java.util.Arrays.asList;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.MethodName.class)
class ProductControllerTests {

    @Autowired
    private MockMvc mvc;
    @Autowired
    private ProductService productService;
    @Autowired
    private MemberService memberService;

    private Member testUserSeller1;
    private Member testUserSeller2;
    private Product product;
    private MockMultipartFile emptyFile;

    @BeforeEach
    void beforeEachTest() {
        emptyFile = new MockMultipartFile("imageFile", new byte[0]);

        testUserSeller1 = memberService.join("testUserSeller1", "김철수", "1234", 2, "testSeller1@test.com").getData();
        testUserSeller2 = memberService.join("testUserSeller2", "김철수", "1234", 2, "testSeller2@test.com").getData();
        product = productService.create(new ProductRequest(testUserSeller1.getId(), "테스트 상품1", "테스트 설명1",
                12000L, 100L, asList(1L, 2L), asList(1L, 2L)), emptyFile).getData();
    }

    @Test
    @DisplayName("상품 등록 폼 처리")
    @WithMockUser(username = "testUserSeller1", authorities = "SELLER")
    void createProductSuccessTest() throws Exception {
        // WHEN
        ResultActions resultActions = mvc
                .perform(multipart("/product/create")
                        .file(emptyFile)
                        .with(csrf())
                        .param("sellerId", "3")
                        .param("name", "테스트상품명1")
                        .param("description", "테스트설명1")
                        .param("price", "1000")
                        .param("stock", "10")
                        .param("nutrientIds", "1,2")
                        .param("categoryIds", "1,2")
                )
                .andDo(print());

        // THEN
        resultActions
                .andExpect(handler().handlerType(ProductController.class))
                .andExpect(handler().methodName("create"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @DisplayName("상품 수정 폼 처리 - 성공")
    @WithMockUser(username = "testUserSeller1", authorities = "SELLER")
    void updateProductSuccessTest() throws Exception {
        // WHEN
        ResultActions resultActions = mvc
                .perform(multipart("/product/update/%s".formatted(product.getId()))
                        .file(emptyFile)
                        .with(csrf())
                        .param("sellerId", String.valueOf(testUserSeller1.getId()))
                        .param("name", "수정상품명")
                        .param("description", "수정설명")
                        .param("price", "1000")
                        .param("stock", "10")
                        .param("nutrientIds", "1,2")
                        .param("categoryIds", "1,2")
                )
                .andDo(print());

        // THEN
        resultActions
                .andExpect(handler().handlerType(ProductController.class))
                .andExpect(handler().methodName("update"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @DisplayName("상품 수정 폼 처리 - 권한없음 실패")
    @WithMockUser(username = "testUserSeller2", authorities = "SELLER")
    void updateProductFailTest() throws Exception {
        // WHEN
        ResultActions resultActions = mvc
                .perform(post("/product/update/%s".formatted(product.getId()))
                        .with(csrf())
                        .param("sellerId", String.valueOf(testUserSeller2))
                        .param("name", "수정상품명")
                        .param("description", "수정설명")
                        .param("price", "1000")
                        .param("stock", "10")
                        .param("nutrientIds", "1,2")
                        .param("categoryIds", "1,2")
                )
                .andDo(print());

        // THEN
        resultActions
                .andExpect(handler().handlerType(ProductController.class))
                .andExpect(handler().methodName("update"))
                .andExpect(status().is4xxClientError());
    }


    @Test
    @DisplayName("상품 삭제 - 성공")
    @WithMockUser(username = "testUserSeller1", authorities = "SELLER")
    void deleteProductSuccessTest() throws Exception {
        // WHEN
        ResultActions resultActions = mvc
                .perform(post("/product/delete/%s".formatted(product.getId()))
                        .with(csrf())
                )
                .andDo(print());

        // THEN
        resultActions
                .andExpect(handler().handlerType(ProductController.class))
                .andExpect(handler().methodName("delete"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @DisplayName("상품 삭제 - 권한없음 실패")
    @WithMockUser(username = "testUserSeller2", authorities = "SELLER")
    void deleteProductFailTest() throws Exception {
        // WHEN
        ResultActions resultActions = mvc
                .perform(post("/product/delete/%s".formatted(product.getId()))
                        .with(csrf())
                )
                .andDo(print());

        // THEN
        resultActions
                .andExpect(handler().handlerType(ProductController.class))
                .andExpect(handler().methodName("delete"))
                .andExpect(status().is3xxRedirection());
    }
}
