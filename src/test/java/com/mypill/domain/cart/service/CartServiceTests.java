package com.mypill.domain.cart.service;

import com.mypill.domain.cart.dto.request.CartProductRequest;
import com.mypill.domain.cart.entity.CartProduct;
import com.mypill.domain.cart.repository.CartProductRepository;
import com.mypill.domain.cart.repository.CartRepository;
import com.mypill.domain.member.entity.Member;
import com.mypill.domain.member.service.MemberService;
import com.mypill.domain.product.dto.request.ProductRequest;
import com.mypill.domain.product.entity.Product;
import com.mypill.domain.product.service.ProductService;
import com.mypill.global.rsData.RsData;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
@Transactional
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.DisplayName.class)
class CartServiceTests {
    @Autowired
    private CartService cartService;
    @Autowired
    private CartProductRepository cartProductRepository;
    @Autowired
    private MemberService memberService;
    @Autowired
    private ProductService productService;

    private Member testUser1;
    private Member testUser2;
    private Product testProduct1;
    private Product testProduct2;

    @BeforeEach
    void beforeEachTest() {
        testUser1 = memberService.join("testUser1", "김철수", "1234", "1", "test1@test.com", true).getData();
        testUser2 = memberService.join("testUser2", "김영희", "1234", "1", "test2@test.com", true).getData();
        Member testUserSeller1 = memberService.join("testUserSeller1", "김철수", "1234", "2", "testSeller1@test.com").getData();
        testProduct1 = productService.create(new ProductRequest(testUserSeller1.getId(), "테스트 상품1", "테스트 설명1", 12000L, 100L, asList(1L, 2L), asList(1L, 2L))).getData();
        testProduct2 = productService.create(new ProductRequest(testUserSeller1.getId(), "테스트 상품2", "테스트 설명2", 12000L, 100L, asList(1L, 2L), asList(1L, 2L))).getData();
    }

    @Test
    @DisplayName("장바구니에 추가 성공")
    void addProductSuccessTest01() {
        RsData<CartProduct> addRsData =  cartService.addProduct(testUser1, new CartProductRequest(testProduct2.getId(), 1L));
        assertThat(addRsData.getResultCode()).isEqualTo("S-1");

        CartProduct cartProduct = cartService.findCartProductById(addRsData.getData().getId()).orElse(null);
        assertThat(cartProduct).isNotNull();
        assertThat(cartProduct.getProduct().getId()).isEqualTo(testProduct2.getId());
    }

    @Test
    @DisplayName("장바구니에 추가 성공 - 이미 담긴 상품")
    void addProductSuccessTest02() {
        RsData<CartProduct> addRsData1 = cartService.addProduct(testUser1, new CartProductRequest(testProduct1.getId(), 1L));
        RsData<CartProduct> addRsData = cartService.addProduct(testUser1, new CartProductRequest(testProduct1.getId(), 1L));
        assertThat(addRsData.getResultCode()).isEqualTo("S-1");

        CartProduct cartProduct = cartService.findCartProductById(addRsData.getData().getId()).orElse(null);
        assertThat(cartProduct).isNotNull();
        assertThat(cartProduct.getQuantity()).isEqualTo(2);
    }

    @Test
    @DisplayName("장바구니에 추가 실패 - 존재하지 않는 상품")
    void addProductFailTest() {
        RsData<CartProduct> addRsData =  cartService.addProduct(testUser1, new CartProductRequest(0L, 1L));
        assertThat(addRsData.getResultCode()).isEqualTo("F-1");
    }

    @Test
    @DisplayName("장바구니에서 상품 수량 변경 실패 - 장바구니에 없는 상품")
    void updateQuantityFailTest() {
        RsData<CartProduct> updateRsData =  cartService.updateQuantity(testUser2, 0L, 3L);
        assertThat(updateRsData.getResultCode()).isEqualTo("F-1");

        CartProduct updatedCartProduct = cartService.findCartProductById(0L).orElse(null);
        assertThat(updatedCartProduct).isNull();
    }

    @Test
    @DisplayName("장바구니에서 상품 삭제 성공")
    void softDeleteCartProductSuccessTest() {
        RsData<CartProduct> addRsData =  cartService.addProduct(testUser1, new CartProductRequest(testProduct1.getId(), 1L));
        RsData<CartProduct> deleteRsData =  cartService.softDeleteCartProduct(testUser1, addRsData.getData().getId());
        assertThat(deleteRsData.getResultCode()).isEqualTo("S-1");

        CartProduct cartProduct = cartService.findCartProductById(addRsData.getData().getId()).orElse(null);
        assertThat(cartProduct).isNotNull();
        assertThat(cartProduct.getDeleteDate()).isNotNull();
    }

    @Test
    @DisplayName("장바구니에서 상품 삭제 실패 - 장바구니에 없는 상품")
    void softDeleteCartProductFailTest() {
        RsData<CartProduct> deleteRsData =  cartService.softDeleteCartProduct(testUser1, testProduct1.getId());
        assertThat(deleteRsData.getResultCode()).isEqualTo("F-1");
    }

}
