package com.mypill.domain.product.controller;


import com.mypill.domain.product.Service.ProductService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.MethodName.class)
public class ProductControllerTests {

    @Autowired
    private MockMvc mvc;
    @Autowired
    private ProductService productService;

    @Test
    @DisplayName("상품 등록 폼")
    void createProductForm() throws Exception {
        // WHEN
        ResultActions resultActions = mvc
                .perform(get("/product/create"))
                .andDo(print());

        // THEN
        resultActions
                .andExpect(handler().handlerType(ProductController.class))
                .andExpect(handler().methodName("showCreate"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().string(containsString("""
                        <input type="text" id="name"
                        """.stripIndent().trim())))
                .andExpect(content().string(containsString("""
                        <input type="number" min="0"  id="price"
                        """.stripIndent().trim())))
                .andExpect(content().string(containsString("""
                        <select id="selectedNutrient"
                        """.stripIndent().trim())));
    }

//    @Test
//    @DisplayName("상품 등록 폼 처리 - 등록 성공")
//    void createProduct() throws Exception {
//        // WHEN
//        ResultActions resultActions = mvc
//                .perform(post("/product/create")
//                        .with(csrf())
//                        .param("name", "테스트상품명1")
//                        .param("description", "테스트설명1")
//                        .
//                )
//                .andDo(print());
//
//        // THEN
//        resultActions
//                .andExpect(handler().handlerType(ProductController.class))
//                .andExpect(handler().methodName("showCreate"))
//                .andExpect(status().is2xxSuccessful())
//                .andExpect(content().string(containsString("""
//                        <input type="text" id="name"
//                        """.stripIndent().trim())))
//                .andExpect(content().string(containsString("""
//                        <input type="number" min="0"  id="price"
//                        """.stripIndent().trim())))
//                .andExpect(content().string(containsString("""
//                        <select id="selectedNutrient"
//                        """.stripIndent().trim())));
//    }

}
