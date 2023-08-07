package com.mypill.domain.order.service;

import com.mypill.domain.address.dto.request.AddressRequest;
import com.mypill.domain.address.entity.Address;
import com.mypill.domain.address.service.AddressService;
import com.mypill.domain.cart.dto.request.CartProductRequest;
import com.mypill.domain.cart.entity.CartProduct;
import com.mypill.domain.cart.service.CartService;
import com.mypill.domain.member.dto.request.JoinRequest;
import com.mypill.domain.member.entity.Member;
import com.mypill.domain.member.service.MemberService;
import com.mypill.domain.order.entity.Order;
import com.mypill.domain.order.entity.OrderItem;
import com.mypill.domain.order.entity.OrderStatus;
import com.mypill.domain.product.dto.request.ProductRequest;
import com.mypill.domain.product.entity.Product;
import com.mypill.domain.product.service.ProductService;
import com.mypill.global.rsdata.RsData;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.DisplayName.class)
@ExtendWith(MockitoExtension.class)
class OrderServiceTests {
    @Autowired
    private CartService cartService;
    @Autowired
    private MemberService memberService;
    @Autowired
    private ProductService productService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private AddressService addressService;

    private Member testUser1;
    private CartProduct cartProduct1;
    private Address address;

    @BeforeEach
    void beforeEachTest() {
        MockMultipartFile emptyFile = new MockMultipartFile("imageFile", new byte[0]);

        testUser1 = memberService.join(new JoinRequest("testUser1", "김철수", "1234", "test1@test.com", "구매자"), true).getData();
        Member testUserSeller1 = memberService.join(new JoinRequest("testUserSeller1", "김철수", "1234", "testSeller1@test.com","판매자")).getData();

        Product testProduct1 = productService.create(new ProductRequest(testUserSeller1.getId(), "테스트 상품1", "테스트 설명1",
                12000L, 100L, asList(1L, 2L), asList(1L, 2L)), emptyFile).getData();
        Product testProduct2 = productService.create(new ProductRequest(testUserSeller1.getId(), "테스트 상품2", "테스트 설명2",
                12000L, 100L, asList(1L, 2L), asList(1L, 2L)), emptyFile).getData();
        cartProduct1 = cartService.addCartProduct(testUser1, new CartProductRequest(testProduct1.getId(), 1L)).getData();
        cartService.addCartProduct(testUser1, new CartProductRequest(testProduct2.getId(), 1L));

        address = addressService.create(new AddressRequest("김철수의 집", "김철수",
                "서울시 강남구", "도산대로1", "12121", "01012341234", true), testUser1).getData();
    }

    @Test
    @DisplayName("장바구니에서 전체 상품 주문 생성")
    void testCreateFromCartSuccess() {
        // WHEN
        RsData<Order> createRsData = orderService.createFromCart(testUser1);
        Order order = createRsData.getData();

        // THEN
        assertThat(createRsData.getResultCode()).isEqualTo("S-1");
        assertThat(order).isNotNull();
        assertThat(order.getPayment()).isNull();
        assertThat(order.getOrderItems()).hasSize(2);
    }

    @Test
    @DisplayName("주문 결제 완료")
    void testPayByTossPaymentsSuccess() {
        //GIVEN
        Order order = orderService.createFromCart(testUser1).getData();

        //WHEN
        orderService.payByTossPayments(order, order.getId() + "_1234", address.getId());

        //THEN
        assertThat(order).isNotNull();
        assertThat(order.getDeliveryAddress()).isNotNull();
        assertThat(order.getPrimaryOrderStatus()).isEqualTo(OrderStatus.ORDERED);
        assertThat(order.getOrderItems().get(0).getStatus()).isEqualTo(OrderStatus.ORDERED);
        assertThat(cartProduct1).isNotNull();
        assertThat(cartProduct1.getDeleteDate()).isNotNull();
        assertThat(cartProduct1.getProduct().getStock()).isEqualTo(99L);
    }

    @Test
    @DisplayName("주문 결제 완료 후 결제정보 업데이트")
    void testUpdatePaymentSuccess() {
        //GIVEN
        Order order = orderService.createFromCart(testUser1).getData();

        //WHEN
        orderService.updatePayment(order, "123", "카드", 24000L, LocalDateTime.now(), "Done");

        //THEN
        assertThat(order).isNotNull();
        assertThat(order.getPayment()).isNotNull();
        assertThat(order.getPayment().getMethod()).isEqualTo("카드");
        assertThat(order.getPayment().getTotalAmount()).isEqualTo(24000L);
    }

    @Test
    @DisplayName("주문 상태 업데이트")
    void testUpdateOrderStatusSuccess() {
        //GIVEN
        Order order = orderService.createFromCart(testUser1).getData();
        orderService.payByTossPayments(order, order.getId() + "_1234", address.getId());

        //WHEN
        OrderItem orderItem = order.getOrderItems().get(0);
        orderService.updateOrderStatus(order.getOrderItems().get(0).getId(), "배송 중");

        //THEN
        assertThat(orderItem.getStatus()).isEqualTo(OrderStatus.SHIPPING);
    }
}
