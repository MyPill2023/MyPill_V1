package com.mypill.domain.cart.controller;

import com.mypill.domain.cart.dto.request.CartProductRequest;
import com.mypill.domain.cart.entity.Cart;
import com.mypill.domain.cart.entity.CartProduct;
import com.mypill.domain.cart.service.CartService;
import com.mypill.global.rsData.RsData;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
public class CartControllerTests {

    @Autowired
    private MockMvc mvc;
    @Autowired
    private CartService cartService;
    private Long cartProductId;

    @BeforeEach
    @WithUserDetails("user1")
    void beforeEachTest() {
        RsData<CartProduct> addRsData = cartService.addProduct(new CartProductRequest(1L, 1L));
        this.cartProductId = addRsData.getData().getId();
    }

    @Test
    @DisplayName("02 장바구니 추가 성공")
    @WithUserDetails("user1")
    void addCartProductSuccessTest() throws Exception {
        ResultActions resultActions = mvc
                .perform(post("/cart/add")
                        .with(csrf())
                        .param("productId","2")
                        .param("quantity","1")
                )
                .andDo(print());

        resultActions
                .andExpect(handler().handlerType(CartController.class))
                .andExpect(handler().methodName("addCartProduct"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/product/detail/2**"));

        Cart cart = cartService.findByMemberId(1L);
        assertThat(cart).isNotNull();

        CartProduct cartProduct = cartService.findCartProductById(2L).orElse(null);
        assertThat(cartProduct).isNotNull();
        assertThat(cartProduct.getCart().getId()).isEqualTo(cart.getId());

    }

    @Test
    @DisplayName("01 장바구니에 담긴 상품 수량 변경 성공")
    @WithUserDetails("user1")
    void addCartProductFailTest() throws Exception {
        CartProduct cartProduct = cartService.findCartProductById(cartProductId).orElse(null);
        assertThat(cartProduct).isNotNull();
        assertThat(cartProduct.getQuantity()).isEqualTo(1);

        ResultActions resultActions = mvc
                .perform(post("/cart/update")
                        .with(csrf())
                        .param("cartProductId",String.valueOf(cartProductId))
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
    @WithUserDetails("user1")
    void softDeleteCartProductSuccessTest() throws Exception {
        CartProduct cartProduct = cartService.findCartProductById(cartProductId).orElse(null);
        assertThat(cartProduct).isNotNull();
        assertThat(cartProduct.getDeleteDate()).isNull();

        ResultActions resultActions = mvc
                .perform(post("/cart/delete")
                        .with(csrf())
                        .param("cartProductId",String.valueOf(cartProductId))
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
