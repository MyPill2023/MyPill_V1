package com.mypill.domain.cart.controller;

import com.mypill.domain.cart.dto.request.CartProductRequest;
import com.mypill.domain.cart.entity.Cart;
import com.mypill.domain.cart.entity.CartProduct;
import com.mypill.domain.cart.service.CartService;
import com.mypill.domain.member.entity.Member;
import com.mypill.domain.member.service.MemberService;
import com.mypill.domain.product.dto.request.ProductRequest;
import com.mypill.domain.product.entity.Product;
import com.mypill.domain.product.service.ProductService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@Transactional
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.DisplayName.class)
class CartControllerTests {

    @Autowired
    private MockMvc mvc;
    @Autowired
    private CartService cartService;
    @Autowired
    private MemberService memberService;
    @Autowired
    private ProductService productService;

    private Member testUser1;
    private Member testUserSeller1;
    private Product product1;
    private Product product2;
    private CartProduct cartProduct;

    @BeforeEach
    void beforeEachTest() {
        testUser1 = memberService.join("testUser1", "김철수", "1234", 1, "test1@test.com").getData();
        testUserSeller1 = memberService.join("testUserSeller1", "김철수", "1234", 2, "testSeller1@test.com").getData();
        product1 = productService.create(new ProductRequest(testUserSeller1.getId(), "테스트 상품1", "테스트 설명1", 12000L, 100L, asList(1L, 2L), asList(1L, 2L))).getData();
        product2 = productService.create(new ProductRequest(testUserSeller1.getId(), "테스트 상품2", "테스트 설명2", 15000L, 100L, asList(3L, 4L), asList(3L, 4L))).getData();
        cartProduct = cartService.addProduct(testUser1, new CartProductRequest(product1.getId(), 1L)).getData();
    }

    @Test
    @DisplayName("01 장바구니 추가 성공")
    @WithMockUser(username = "testUser1", authorities = "MEMBER")
    void addCartProductSuccessTest() throws Exception {
        ResultActions resultActions = mvc
                .perform(post("/cart/add")
                        .with(csrf())
                        .param("productId", String.valueOf(product2.getId()))
                        .param("quantity","1")
                )
                .andDo(print());

        resultActions
                .andExpect(handler().handlerType(CartController.class))
                .andExpect(handler().methodName("addCartProduct"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/product/detail/**"));
    }

    @Test
    @DisplayName("02 장바구니에 담긴 상품 수량 변경 성공")
    @WithMockUser(username = "testUser1", authorities = "MEMBER")
    void addCartProductFailTest() throws Exception {
        CartProduct cartProduct = cartService.findCartProductById(this.cartProduct.getId()).orElse(null);
        assertThat(cartProduct).isNotNull();
        assertThat(cartProduct.getQuantity()).isEqualTo(1);

        ResultActions resultActions = mvc
                .perform(post("/cart/update")
                        .with(csrf())
                        .param("cartProductId",String.valueOf(this.cartProduct.getId()))
                        .param("newQuantity","3")
                )
                .andDo(print());

        resultActions
                .andExpect(handler().handlerType(CartController.class))
                .andExpect(handler().methodName("updateQuantity"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/cart**"));

        assertThat(cartProduct.getQuantity()).isEqualTo(3);
    }

    @Test
    @DisplayName("03 장바구니에 담긴 상품 삭제 성공")
    @WithMockUser(username = "testUser1", authorities = "MEMBER")
    void softDeleteCartProductSuccessTest() throws Exception {
        CartProduct cartProduct = cartService.findCartProductById(this.cartProduct.getId()).orElse(null);
        assertThat(cartProduct).isNotNull();
        assertThat(cartProduct.getDeleteDate()).isNull();

        ResultActions resultActions = mvc
                .perform(post("/cart/delete")
                        .with(csrf())
                        .param("cartProductId",String.valueOf(this.cartProduct.getId()))
                )
                .andDo(print());

        resultActions
                .andExpect(handler().handlerType(CartController.class))
                .andExpect(handler().methodName("softDeleteCartProduct"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/cart**"));

        assertThat(cartProduct.getDeleteDate()).isNotNull();
    }
}
