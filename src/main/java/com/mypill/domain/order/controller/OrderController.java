package com.mypill.domain.order.controller;

import com.mypill.domain.address.dto.response.AddressResponse;
import com.mypill.domain.address.service.AddressService;
import com.mypill.domain.member.entity.Member;
import com.mypill.domain.order.dto.request.PayRequest;
import com.mypill.domain.order.dto.response.OrderItemResponse;
import com.mypill.domain.order.dto.response.OrderResponse;
import com.mypill.domain.order.dto.response.PayResponse;
import com.mypill.domain.order.entity.Order;
import com.mypill.domain.order.entity.OrderItem;
import com.mypill.domain.order.entity.OrderStatus;
import com.mypill.domain.order.service.OrderService;
import com.mypill.global.rq.Rq;
import com.mypill.global.rsdata.RsData;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/order")
@Tag(name = "OrderController", description = "주문")
public class OrderController {

    private final OrderService orderService;
    private final AddressService addressService;
    private final Rq rq;

    @PreAuthorize("hasAuthority('BUYER')")
    @GetMapping("/form/{orderId}")
    @Operation(summary = "주문하기 페이지")
    public String getOrderForm(@PathVariable Long orderId, Model model) {
        RsData<Order> rsData = orderService.getOrderForm(rq.getMember(), orderId);
        if (rsData.isFail()) {
            return rq.historyBack(rsData);
        }
        List<AddressResponse> addresses = addressService.findByMemberId(rq.getMember().getId()).stream()
                .filter(address -> address.getDeleteDate() == null)
                .map(AddressResponse::of)
                .toList();
        AddressResponse defaultAddress = addresses.stream()
                .filter(AddressResponse::isDefault)
                .findFirst().orElse(null);
        model.addAttribute("order", OrderResponse.of(rsData.getData()));
        model.addAttribute("addresses", addresses);
        model.addAttribute("defaultAddress", defaultAddress);
        return "usr/order/form";
    }

    @PreAuthorize("hasAuthority('BUYER')")
    @PostMapping("/create/all")
    @Operation(summary = "장바구니의 전체 상품 주문")
    public String createFromCart() {
        Member buyer = rq.getMember();
        RsData<Order> orderRsData = orderService.createFromCart(buyer);
        if (orderRsData.isFail()) {
            return rq.historyBack(orderRsData);
        }
        return rq.redirectWithMsg("/order/form/%s".formatted(orderRsData.getData().getId()), orderRsData);
    }

    @PreAuthorize("hasAuthority('BUYER')")
    @PostMapping("/create/selected")
    @Operation(summary = "장바구니에서 선택한 상품만 주문")
    public String createFromSelected(@RequestParam String[] selectedCartProductIds) {
        Member buyer = rq.getMember();
        if (selectedCartProductIds.length == 0) {
            return rq.historyBack("선택된 상품이 없습니다.");
        }
        RsData<Order> orderRsData = orderService.createFromSelectedCartProduct(buyer, selectedCartProductIds);
        if (orderRsData.isFail()) {
            return rq.historyBack(orderRsData);
        }
        return rq.redirectWithMsg("/order/form/%s".formatted(orderRsData.getData().getId()), orderRsData);
    }

    @PreAuthorize("hasAuthority('BUYER')")
    @PostMapping("/create/single")
    @Operation(summary = "개별 상품 바로 주문")
    public String createFromSingleProduct(@RequestParam Long productId, @RequestParam Long quantity) {
        Member buyer = rq.getMember();
        RsData<Order> orderRsData = orderService.createSingleProduct(buyer, productId, quantity);
        if (orderRsData.isFail()) {
            return rq.historyBack(orderRsData);
        }
        return rq.redirectWithMsg("/order/form/%s".formatted(orderRsData.getData().getId()), orderRsData);
    }

    @PreAuthorize("hasAuthority('BUYER')")
    @GetMapping("/detail/{orderId}")
    @Operation(summary = "구매자의 주문 내역 조회 페이지")
    public String getOrderDetail(@PathVariable Long orderId, Model model) {
        RsData<Order> rsData = orderService.getOrderDetail(orderId);
        if (rsData.isFail()) {
            return rq.historyBack(rsData);
        }
        model.addAttribute("order", OrderResponse.of(rsData.getData()));
        return "usr/order/detail";
    }

    @PreAuthorize("hasAuthority('BUYER')")
    @GetMapping("/{id}/success")
    @Operation(summary = "결제 성공")
    public String confirmPayment(@PathVariable long id, PayRequest payRequest, Model model) {
        RsData<Order> validateRsData = orderService.validateOrder(id, payRequest.getOrderId(), payRequest.getAmount());
        if (validateRsData.isFail()) {
            return rq.historyBack(validateRsData);
        }
        RsData<?> payRsData = orderService.pay(validateRsData.getData(), payRequest);
        if (payRsData.isFail()) {
            if (payRsData.getResultCode().equals("F-2")) {
                model.addAttribute("payResponse", payRsData.getData());
            }
            return rq.historyBack(payRsData);
        }
        return rq.redirectWithMsg("/order/detail/%s".formatted(((Order) payRsData.getData()).getId()), "주문이 완료되었습니다.");
    }

    @PreAuthorize("hasAuthority('BUYER')")
    @GetMapping("/{id}/fail")
    @Operation(summary = "결제 실패")
    public String failPayment(PayResponse payResponse, Model model) {
        model.addAttribute("payResponse", payResponse);
        return "usr/order/fail";
    }

    @PreAuthorize("hasAuthority('BUYER')")
    @PostMapping("/cancel/{orderId}")
    @Operation(summary = "주문 취소")
    public String cancel(@PathVariable Long orderId) {
        RsData<Order> checkRsData = orderService.checkCanCancel(rq.getMember(), orderId);
        if (checkRsData.isFail()) {
            return rq.historyBack(checkRsData);
        }
        RsData<?> cancelRsData = orderService.cancel(checkRsData.getData());
        if (cancelRsData.isFail()) {
            return rq.historyBack(cancelRsData);
        }
        return rq.redirectWithMsg("/order/detail/%s".formatted(((Order) cancelRsData.getData()).getId()), cancelRsData.getMsg());
    }

    @PreAuthorize("hasAuthority('SELLER')")
    @GetMapping("/management/{orderId}")
    @Operation(summary = "판매자의 주문 관리 페이지")
    public String management(@PathVariable Long orderId, Model model) {
        RsData<Order> rsData = orderService.getOrderDetail(orderId);
        if (rsData.isFail()) {
            return rq.historyBack(rsData);
        }
        Order order = rsData.getData();
        List<OrderItemResponse> orderItemResponses = order.getOrderItems().stream()
                .filter(orderItem -> orderItem.getProduct().getSeller().getId().equals(rq.getMember().getId()))
                .map(OrderItemResponse::of)
                .toList();
        model.addAttribute("order", OrderResponse.of(order));
        model.addAttribute("orderItems", orderItemResponses);
        List<OrderStatus> orderStatuses = Arrays.asList(OrderStatus.values());
        model.addAttribute("orderStatuses", orderStatuses);
        return "usr/order/management";
    }

    @PreAuthorize("hasAuthority('SELLER')")
    @PostMapping("/update/status/{orderItemId}")
    @Operation(summary = "판매자의 주문 상태 업데이트")
    public String updateOrderStatus(@PathVariable Long orderItemId, @RequestParam Long orderId, @RequestParam String newStatus) {
        RsData<OrderItem> updateRsData = orderService.updateOrderStatus(orderItemId, newStatus);
        if (updateRsData.isFail()) {
            rq.historyBack(updateRsData);
        }
        return rq.redirectWithMsg("/order/management/%s".formatted(orderId), updateRsData);
    }
}
