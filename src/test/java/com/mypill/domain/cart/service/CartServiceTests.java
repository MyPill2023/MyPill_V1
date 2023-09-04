package com.mypill.domain.cart.service;

import com.mypill.domain.cart.dto.request.CartProductRequest;
import com.mypill.domain.cart.entity.CartProduct;
import com.mypill.domain.member.dto.request.JoinRequest;
import com.mypill.domain.member.entity.Member;
import com.mypill.domain.member.service.MemberService;
import com.mypill.domain.product.dto.request.ProductRequest;
import com.mypill.domain.product.entity.Product;
import com.mypill.domain.product.service.ProductService;
import com.mypill.global.rsdata.RsData;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@TestMethodOrder(MethodOrderer.DisplayName.class)
class CartServiceTests {
    @Autowired
    private CartService cartService;
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
        MockMultipartFile emptyFile = new MockMultipartFile("imageFile", new byte[0]);
        testUser1 = memberService.join(new JoinRequest("testUser1", "김철수", "1234", "test1@test.com", "구매자"), true).getData();
        testUser2 = memberService.join(new JoinRequest("testUser2", "김영희", "1234", "test2@test.com", "구매자"), true).getData();
        Member testUserSeller1 = memberService.join(new JoinRequest("testUserSeller1", "김철수", "1234", "testSeller1@test.com", "판매자")).getData();
        testProduct1 = productService.create(new ProductRequest("테스트 상품1", "테스트 설명1",
                12000L, 100L, asList(1L, 2L), asList(1L, 2L), emptyFile), testUserSeller1).getData();
        testProduct2 = productService.create(new ProductRequest("테스트 상품2", "테스트 설명2",
                12000L, 100L, asList(1L, 2L), asList(1L, 2L), emptyFile), testUserSeller1).getData();
    }

    @Test
    @DisplayName("장바구니에 추가 성공")
    void addProductSuccessTest01() {
        // WHEN
        RsData<CartProduct> addRsData = cartService.addCartProduct(testUser1, new CartProductRequest(testProduct2.getId(), 1L));
        CartProduct cartProduct = cartService.findCartProductById(addRsData.getData().getId()).orElse(null);

        // THEN
        assertThat(addRsData.getResultCode()).isEqualTo("S-1");
        assertThat(cartProduct).isNotNull();
        assertThat(cartProduct.getProduct().getId()).isEqualTo(testProduct2.getId());
    }

    @Test
    @DisplayName("장바구니에 추가 성공 - 이미 담긴 상품")
    void addProductSuccessTest02() {
        // GIVEN
        cartService.addCartProduct(testUser1, new CartProductRequest(testProduct1.getId(), 1L));

        // WHEN
        RsData<CartProduct> addRsData = cartService.addCartProduct(testUser1, new CartProductRequest(testProduct1.getId(), 1L));
        CartProduct cartProduct = cartService.findCartProductById(addRsData.getData().getId()).orElse(null);

        // THEN
        assertThat(addRsData.getResultCode()).isEqualTo("S-1");
        assertThat(cartProduct).isNotNull();
        assertThat(cartProduct.getQuantity()).isEqualTo(2);
    }

    @Test
    @DisplayName("장바구니에 추가 실패 - 존재하지 않는 상품")
    void addProductFailTest() {
        // WHEN
        RsData<CartProduct> addRsData = cartService.addCartProduct(testUser1, new CartProductRequest(0L, 1L));

        // THEN
        assertThat(addRsData.getResultCode()).isEqualTo("F-1");
    }


    @Test
    @DisplayName("장바구니에서 상품 수량 변경 성공")
    void updateQuantitySuccessTest() {
        // GIVEN
        CartProduct cartProduct = cartService.addCartProduct(testUser1, new CartProductRequest(testProduct2.getId(), 1L)).getData();

        // WHEN
        RsData<CartProduct> updateRsData = cartService.updateCartProductQuantity(testUser1, cartProduct.getId(), 3L);
        CartProduct updatedCartProduct = cartService.findCartProductById(updateRsData.getData().getId()).orElse(null);

        // THEN
        assertThat(updateRsData.getResultCode()).isEqualTo("S-1");
        assertThat(updatedCartProduct).isNotNull();
        assertThat(updatedCartProduct.getQuantity()).isEqualTo(3);
    }

    @Test
    @DisplayName("장바구니에서 상품 수량 변경 실패 - 장바구니에 없는 상품")
    void updateQuantityFailTest() {
        // WHEN
        RsData<CartProduct> updateRsData = cartService.updateCartProductQuantity(testUser2, 0L, 3L);
        CartProduct updatedCartProduct = cartService.findCartProductById(0L).orElse(null);

        // THEN
        assertThat(updateRsData.getResultCode()).isEqualTo("F-1");
        assertThat(updatedCartProduct).isNull();
    }

    @Test
    @DisplayName("장바구니에서 상품 삭제 성공")
    void hardDeleteCartProductSuccessTest() {
        // GIVEN
        RsData<CartProduct> addRsData = cartService.addCartProduct(testUser1, new CartProductRequest(testProduct1.getId(), 1L));
        Long cartProductId = addRsData.getData().getId();
        CartProduct cartProduct = cartService.findCartProductById(cartProductId).orElse(null);
        assertThat(cartProduct).isNotNull();

        // WHEN
        RsData<CartProduct> deleteRsData = cartService.hardDeleteCartProduct(testUser1, cartProductId);
        cartProduct = cartService.findCartProductById(cartProductId).orElse(null);

        // THEN
        assertThat(deleteRsData.getResultCode()).isEqualTo("S-1");
        assertThat(cartProduct).isNull();
    }

    @Test
    @DisplayName("장바구니에서 상품 삭제 실패 - 장바구니에 없는 상품")
    void hardDeleteCartProductFailTest() {
        // WHEN
        RsData<CartProduct> deleteRsData = cartService.hardDeleteCartProduct(testUser1, testProduct1.getId());

        // THEN
        assertThat(deleteRsData.getResultCode()).isEqualTo("F-1");
    }
}
