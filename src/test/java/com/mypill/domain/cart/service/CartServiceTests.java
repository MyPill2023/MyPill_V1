package com.mypill.domain.cart.service;

import com.mypill.domain.cart.dto.request.CartProductRequest;
import com.mypill.domain.cart.entity.CartProduct;
import com.mypill.global.rsData.RsData;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
@Transactional
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.MethodName.class)
public class CartServiceTests {
    @Autowired
    private CartService cartService;

    @Test
    @DisplayName("장바구니에 추가 성공")
    @WithUserDetails("user1")
    void addProductSuccessTest01() throws Exception {
        RsData<CartProduct> addRsData =  cartService.addProduct(new CartProductRequest(1L, 1L));
        assertThat(addRsData.getResultCode()).isEqualTo("S-1");

        CartProduct cartProduct = cartService.findCartProductById(1L).orElse(null);
        assertThat(cartProduct).isNotNull();
        assertThat(cartProduct.getProduct().getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("장바구니에 추가 성공 - 이미 담긴 상품")
    @WithUserDetails("user1")
    void addProductSuccessTest02() throws Exception {
        cartService.addProduct(new CartProductRequest(1L, 1L));
        RsData<CartProduct> addRsData =  cartService.addProduct(new CartProductRequest(1L, 1L));
        assertThat(addRsData.getResultCode()).isEqualTo("S-2");

        CartProduct cartProduct = cartService.findCartProductById(addRsData.getData().getId()).orElse(null);
        assertThat(cartProduct).isNotNull();
        assertThat(cartProduct.getProduct().getId()).isEqualTo(1L);
        assertThat(cartProduct.getQuantity()).isEqualTo(2);

    }

    @Test
    @DisplayName("장바구니에 추가 실패 - 존재하지 않는 상품")
    @WithUserDetails("user1")
    void addProductFailTest() throws Exception {
        RsData<CartProduct> addRsData =  cartService.addProduct(new CartProductRequest(6L, 1L));
        assertThat(addRsData.getResultCode()).isEqualTo("F-1");
    }

    @Test
    @DisplayName("장바구니에서 상품 수량 변경 선공")
    @WithUserDetails("user1")
    void updateQuantitySuccessTest() throws Exception {
        RsData<CartProduct> addRsData =  cartService.addProduct(new CartProductRequest(1L, 1L));
        RsData<CartProduct> updateRsData =  cartService.updateQuantity(addRsData.getData().getId(), 3L);
        assertThat(updateRsData.getResultCode()).isEqualTo("S-1");

        CartProduct cartProduct = cartService.findCartProductById(addRsData.getData().getId()).orElse(null);
        assertThat(cartProduct).isNotNull();
        assertThat(cartProduct.getProduct().getId()).isEqualTo(1L);
        assertThat(cartProduct.getQuantity()).isEqualTo(3);
    }

    @Test
    @DisplayName("장바구니에서 상품 수량 변경 실패 - 장바구니에 없는 상품")
    @WithUserDetails("user1")
    void updateQuantityFailTest() throws Exception {
        RsData<CartProduct> updateRsData =  cartService.updateQuantity(2L, 3L);
        assertThat(updateRsData.getResultCode()).isEqualTo("F-1");

        CartProduct cartProduct = cartService.findCartProductById(1L).orElse(null);
        assertThat(cartProduct).isNull();
    }

    @Test
    @DisplayName("장바구니에서 상품 삭제 성공")
    @WithUserDetails("user1")
    void softDeleteCartProductSuccessTest() throws Exception {
        RsData<CartProduct> addRsData =  cartService.addProduct(new CartProductRequest(1L, 1L));
        RsData<CartProduct> deleteRsData =  cartService.softDeleteCartProduct(addRsData.getData().getId());
        assertThat(deleteRsData.getResultCode()).isEqualTo("S-1");

        CartProduct cartProduct = cartService.findCartProductById(addRsData.getData().getId()).orElse(null);
        assertThat(cartProduct).isNotNull();
        assertThat(cartProduct.getDeleteDate()).isNotNull();
    }

    @Test
    @DisplayName("장바구니에서 상품 삭제 실패 - 장바구니에 없는 상품")
    @WithUserDetails("user1")
    void softDeleteCartProductFailTest() throws Exception {
        RsData<CartProduct> deleteRsData =  cartService.softDeleteCartProduct(2L);
        assertThat(deleteRsData.getResultCode()).isEqualTo("F-1");
    }

}
