package com.mypill.domain.product.controller;


import com.mypill.domain.category.entity.Category;
import com.mypill.domain.category.service.CategoryService;
import com.mypill.domain.nutrient.Service.NutrientService;
import com.mypill.domain.nutrient.entity.Nutrient;
import com.mypill.domain.product.Service.ProductService;
import com.mypill.domain.product.dto.response.ProductResponse;
import com.mypill.domain.product.entity.Product;
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

import java.util.List;
import java.util.stream.Collectors;

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
    @DisplayName("상품 등록 폼 처리")
    @WithUserDetails("user3")
    void createProduct() throws Exception {
        // WHEN
        ResultActions resultActions = mvc
                .perform(post("/product/create")
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
    @WithUserDetails("user3")
    void updateProductSuccess() throws Exception {
        // WHEN
        ResultActions resultActions = mvc
                .perform(post("/product/update/1")
                        .with(csrf())
                        .param("sellerId", "3")
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

        Product updatedproduct = productService.findById(1L).orElse(null);
        assertThat(updatedproduct).isNotNull();
        assertThat(updatedproduct.getName()).isEqualTo("수정상품명");
        assertThat(updatedproduct.getDescription()).isEqualTo("수정설명");
    }

    @Test
    @DisplayName("상품 수정 폼 처리 - 권한없음 실패")
    @WithUserDetails("user4")
    void updateProductFail() throws Exception {
        // WHEN
        ResultActions resultActions = mvc
                .perform(post("/product/update/1")
                        .with(csrf())
                        .param("sellerId", "4")
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

        Product updatedproduct = productService.findById(1L).orElse(null);
        assertThat(updatedproduct).isNotNull();
        assertThat(updatedproduct.getName()).isEqualTo("루테인 베스트");
    }


    @Test
    @DisplayName("상품 삭제 - 성공")
    @WithUserDetails("user3")
    void deleteProductSuccess() throws Exception {
        // WHEN
        ResultActions resultActions = mvc
                .perform(post("/product/delete/1")
                        .with(csrf())
                )
                .andDo(print());

        // THEN
        resultActions
                .andExpect(handler().handlerType(ProductController.class))
                .andExpect(handler().methodName("delete"))
                .andExpect(status().is3xxRedirection());

        Product deletedproduct = productService.findById(1L).orElse(null);
        assertThat(deletedproduct).isNotNull();
        assertThat(deletedproduct.getDeleteDate()).isNotNull();
    }

    @Test
    @DisplayName("상품 삭제 - 권한없음 실패")
    @WithUserDetails("user4")
    void deleteProductFail() throws Exception {
        // WHEN
        ResultActions resultActions = mvc
                .perform(post("/product/delete/1")
                        .with(csrf())
                )
                .andDo(print());

        // THEN
        resultActions
                .andExpect(handler().handlerType(ProductController.class))
                .andExpect(handler().methodName("delete"))
                .andExpect(status().is3xxRedirection());

        Product deletedproduct = productService.findById(1L).orElse(null);
        assertThat(deletedproduct).isNotNull();
        assertThat(deletedproduct.getDeleteDate()).isNull();
    }

}
