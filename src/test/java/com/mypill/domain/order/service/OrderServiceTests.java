package com.mypill.domain.order.service;

import com.mypill.domain.address.dto.request.AddressRequest;
import com.mypill.domain.address.entity.Address;
import com.mypill.domain.address.service.AddressService;
import com.mypill.domain.cart.dto.request.CartProductRequest;
import com.mypill.domain.cart.service.CartService;
import com.mypill.domain.member.dto.request.JoinRequest;
import com.mypill.domain.member.entity.Member;
import com.mypill.domain.member.service.MemberService;
import com.mypill.domain.order.entity.Order;
import com.mypill.domain.order.entity.OrderItem;
import com.mypill.domain.order.entity.OrderStatus;
import com.mypill.domain.order.entity.Payment;
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
    private Address address;
    private Payment payment;

    @BeforeEach
    void beforeEachTest() {
        MockMultipartFile emptyFile = new MockMultipartFile("imageFile", new byte[0]);

        testUser1 = memberService.join(new JoinRequest("testUser1", "김철수", "1234", "test1@test.com", "구매자"), true).getData();
        Member testUserSeller1 = memberService.join(new JoinRequest("testUserSeller1", "김철수", "1234", "testSeller1@test.com","판매자")).getData();

        Product testProduct1 = productService.create(new ProductRequest( "테스트 상품1", "테스트 설명1",
                12000L, 100L, asList(1L, 2L), asList(1L, 2L), emptyFile), testUserSeller1).getData();
        Product testProduct2 = productService.create(new ProductRequest("테스트 상품2", "테스트 설명2",
                12000L, 100L, asList(1L, 2L), asList(1L, 2L), emptyFile), testUserSeller1).getData();
        cartService.addCartProduct(testUser1, new CartProductRequest(testProduct1.getId(), 1L));
        cartService.addCartProduct(testUser1, new CartProductRequest(testProduct2.getId(), 1L));

        address = addressService.create(new AddressRequest("김철수의 집", "김철수",
                "서울시 강남구", "도산대로1", "12121", "01012341234", true), testUser1).getData();
        payment = new Payment("123", "카드", 24000L, LocalDateTime.now(), "DONE");
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
    void testUpdateOrderAsPaymentDoneSuccess() {
        //GIVEN
        Order order = orderService.createFromCart(testUser1).getData();

        //WHEN
        orderService.updateOrderAsPaymentDone(order, order.getId() + "_1234", address.getId(), payment);

        //THEN
        assertThat(order).isNotNull();
        assertThat(order.getDeliveryAddress()).isNotNull();
        assertThat(order.getPrimaryOrderStatus()).isEqualTo(OrderStatus.ORDERED);
        assertThat(order.getOrderItems().get(0).getStatus()).isEqualTo(OrderStatus.ORDERED);
    }

    @Test
    @DisplayName("주문 상태 업데이트")
    void testUpdateOrderItemStatusSuccess() {
        //GIVEN
        Order order = orderService.createFromCart(testUser1).getData();
        orderService.updateOrderAsPaymentDone(order, order.getId() + "_1234", address.getId(), payment);

        //WHEN
        OrderItem orderItem = order.getOrderItems().get(0);
        orderService.updateOrderItemStatus(order.getOrderItems().get(0).getId(), "배송 중");

        //THEN
        assertThat(orderItem.getStatus()).isEqualTo(OrderStatus.SHIPPING);
        assertThat(order.getPayment()).isNotNull();
        assertThat(order.getPayment().getMethod()).isEqualTo("카드");
        assertThat(order.getPayment().getTotalAmount()).isEqualTo(24000L);
    }
}
