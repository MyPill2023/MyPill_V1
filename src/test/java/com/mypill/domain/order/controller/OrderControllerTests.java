package com.mypill.domain.order.controller;

import com.mypill.domain.address.dto.request.AddressRequest;
import com.mypill.domain.address.entity.Address;
import com.mypill.domain.address.service.AddressService;
import com.mypill.domain.cart.dto.request.CartProductRequest;
import com.mypill.domain.cart.entity.CartProduct;
import com.mypill.domain.cart.service.CartService;
import com.mypill.domain.member.entity.Member;
import com.mypill.domain.member.service.MemberService;
import com.mypill.domain.order.entity.Order;
import com.mypill.domain.order.entity.OrderItem;
import com.mypill.domain.order.service.OrderService;
import com.mypill.domain.product.dto.request.ProductRequest;
import com.mypill.domain.product.entity.Product;
import com.mypill.domain.product.service.ProductService;
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

import java.time.LocalDateTime;

import static java.util.Arrays.asList;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.DisplayName.class)
class OrderControllerTests {

    @Autowired
    private MockMvc mvc;
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
    private Product testProduct1;
    private CartProduct cartProduct1;
    private Address address;

    @BeforeEach
    void beforeEachTest() {
        MockMultipartFile emptyFile = new MockMultipartFile("imageFile", new byte[0]);

        testUser1 = memberService.join("testUser1", "김철수", "1234", "1", "test1@test.com", true).getData();
        memberService.join("testUser2", "김영희", "1234", "1", "test2@test.com", true);
        Member testUserSeller1 = memberService.join("testUserSeller1", "김철수", "1234", 2, "testSeller1@test.com").getData();

        testProduct1 = productService.create(new ProductRequest(testUserSeller1.getId(), "테스트 상품1", "테스트 설명1",
                12000L, 100L, asList(1L, 2L), asList(1L, 2L)), emptyFile).getData();
        Product testProduct2 = productService.create(new ProductRequest(testUserSeller1.getId(), "테스트 상품2", "테스트 설명2",
                12000L, 100L, asList(1L, 2L), asList(1L, 2L)), emptyFile).getData();

        cartProduct1 = cartService.addCartProduct(testUser1, new CartProductRequest(testProduct1.getId(), 1L)).getData();
        cartService.addCartProduct(testUser1, new CartProductRequest(testProduct2.getId(), 1L));

        address = addressService.create(new AddressRequest("김철수의 집", "김철수", "서울시 강남구", "도산대로1", "12121", "01012341234", true), testUser1).getData();

    }

    @Test
    @DisplayName("전체 상품 주문 성공")
    @WithMockUser(username = "testUser1", authorities = "BUYER")
    void testCreateFromCartSuccess() throws Exception {
        //WHEN
        ResultActions resultActions = mvc
                .perform(post("/order/create/all")
                        .with(csrf()))
                .andDo(print());

        //THEN
        resultActions
                .andExpect(handler().handlerType(OrderController.class))
                .andExpect(handler().methodName("createFromCart"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/order/form/**"));
    }

    @Test
    @DisplayName("선택 상품 주문 성공")
    @WithMockUser(username = "testUser1", authorities = "BUYER")
    void testCreateFromSelectedSuccess() throws Exception {
        //WHEN
        ResultActions resultActions = mvc
                .perform(post("/order/create/selected")
                        .with(csrf())
                        .param("selectedCartProductIds", String.valueOf(cartProduct1.getProduct().getId())))
                .andDo(print());

        //THEN
        resultActions
                .andExpect(handler().handlerType(OrderController.class))
                .andExpect(handler().methodName("createFromSelected"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/order/form/**"));
    }

    @Test
    @DisplayName("개별 상품 주문 성공")
    @WithMockUser(username = "testUser1", authorities = "BUYER")
    void testCreateFromSingleProductSuccess() throws Exception {
        //WHEN
        ResultActions resultActions = mvc
                .perform(post("/order/create/single")
                        .with(csrf())
                        .param("productId", String.valueOf(testProduct1.getId()))
                        .param("quantity", "1"))
                .andDo(print());

        //THEN
        resultActions
                .andExpect(handler().handlerType(OrderController.class))
                .andExpect(handler().methodName("createFromSingleProduct"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/order/form/**"));

    }

    @Test
    @DisplayName("주문 폼 가져오기 성공")
    @WithMockUser(username = "testUser1", authorities = "BUYER")
    void testGetOrderFormSuccess() throws Exception {
        //GIVEN
        Order order = orderService.createFromCart(testUser1).getData();

        //WHEN
        ResultActions resultActions = mvc
                .perform(get("/order/form/%s".formatted(String.valueOf(order.getId()))))
                .andDo(print());

        //THEN
        resultActions
                .andExpect(handler().handlerType(OrderController.class))
                .andExpect(handler().methodName("getOrderForm"))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    @DisplayName("주문 폼 가져오기 실패 - 다른 사람의 주문")
    @WithMockUser(username = "testUser2", authorities = "BUYER")
    void testGetOrderFormFail() throws Exception {
        //GIVEN
        Order order = orderService.createFromCart(testUser1).getData();

        //WHEN
        ResultActions resultActions = mvc
                .perform(get("/order/form/%s".formatted(String.valueOf(order.getId()))))
                .andDo(print());

        //THEN
        resultActions
                .andExpect(handler().handlerType(OrderController.class))
                .andExpect(handler().methodName("getOrderForm"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @DisplayName("주문 상세 정보")
    @WithMockUser(username = "testUser1", authorities = "BUYER")
    void testGetOrderDetailSuccess() throws Exception {
        //GIVEN
        Order order = orderService.createFromCart(testUser1).getData();
        orderService.payByTossPayments(order, order.getId() + "_1234", address.getId());
        orderService.updatePayment(order, "123", "카드", 24000L, LocalDateTime.now(), "Done");

        //WHEN
        ResultActions resultActions = mvc
                .perform(get("/order/detail/%s".formatted(String.valueOf(order.getId()))))
                .andDo(print());
        //THEN
        resultActions
                .andExpect(handler().handlerType(OrderController.class))
                .andExpect(handler().methodName("getOrderDetail"))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    @DisplayName("주문 상품 상태 변경")
    @WithMockUser(username = "testUserSeller1", authorities = "SELLER")
    void testUpdateOrderStatusSuccess() throws Exception {
        //GIVEN
        Order order = orderService.createFromCart(testUser1).getData();
        orderService.payByTossPayments(order, order.getId() + "_1234", address.getId());
        orderService.updatePayment(order, "123", "카드", 24000L, LocalDateTime.now(), "Done");
        OrderItem orderItem = order.getOrderItems().get(0);

        //WHEN
        ResultActions resultActions = mvc
                .perform(post("/order/update/status/%s".formatted(String.valueOf(orderItem.getId())))
                        .with(csrf())
                        .param("orderId", String.valueOf(order.getId()))
                        .param("newStatus", "배송 중"))
                .andDo(print());

        //THEN
        resultActions
                .andExpect(handler().handlerType(OrderController.class))
                .andExpect(handler().methodName("updateOrderStatus"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/order/management/%s**".formatted(order.getId())));
    }
}
