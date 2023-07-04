package com.mypill.domain.order.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mypill.domain.address.dto.response.AddressResponse;
import com.mypill.domain.address.service.AddressService;
import com.mypill.domain.member.entity.Member;
import com.mypill.domain.order.dto.response.OrderItemResponse;
import com.mypill.domain.order.dto.response.OrderResponse;
import com.mypill.domain.order.entity.Order;
import com.mypill.domain.order.entity.OrderItem;
import com.mypill.domain.order.entity.OrderStatus;
import com.mypill.domain.order.service.OrderService;
import com.mypill.global.AppConfig;
import com.mypill.global.rq.Rq;
import com.mypill.global.rsData.RsData;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;


@Controller
@RequiredArgsConstructor
@RequestMapping("/order")
public class OrderController {

    private final OrderService orderService;
    private final AddressService addressService;
    private final Rq rq;
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper;

    @GetMapping("/form/{orderId}")
    @PreAuthorize("hasAuthority('MEMBER')")
    public String getOrderForm(@PathVariable Long orderId, Model model) {
        RsData<OrderResponse> rsData = orderService.getOrderForm(orderId);
        if (rsData.isFail()) {
            return rq.historyBack(rsData);
        }
        model.addAttribute("orderResponse", rsData.getData());

        List<AddressResponse> addresses = addressService.findByMemberId(rq.getMember().getId()).stream()
                .filter(address -> address.getDeleteDate() == null)
                .map(AddressResponse::of)
                .toList();
        AddressResponse defaultAddress = addresses.stream()
                .filter(AddressResponse::isDefault)
                .findFirst().orElse(null);

        model.addAttribute("addresses", addresses);
        model.addAttribute("defaultAddress", defaultAddress);

        return "usr/order/form";
    }

    @PostMapping("/create/all")
    @PreAuthorize("hasAuthority('MEMBER')")
    public String createFromCart() {
        Member buyer = rq.getMember();
        RsData<Order> orderRsData = orderService.createFromCart(buyer);

        if (orderRsData.isFail()) {
            return rq.historyBack(orderRsData);
        }

        return rq.redirectWithMsg("/order/form/%s".formatted(orderRsData.getData().getId()), orderRsData);
    }

    @PostMapping("/create")
    @PreAuthorize("hasAuthority('MEMBER')")
    public String createFromSelected(@RequestParam String[] selectedCartProductIds) {
        Member buyer = rq.getMember();

        if(selectedCartProductIds.length == 0){
            return rq.historyBack("선택된 상품이 없습니다.");
        }

        RsData<Order> orderRsData = orderService.createFromSelectedCartProduct(buyer, selectedCartProductIds);
        if (orderRsData.isFail()) {
            return rq.historyBack(orderRsData);
        }

        return rq.redirectWithMsg("/order/form/%s".formatted(orderRsData.getData().getId()), orderRsData);
    }

    @GetMapping("/detail/{orderId}")
    @PreAuthorize("isAuthenticated()")
    public String getOrderDetail(@PathVariable Long orderId, Model model) {

        RsData<Order> rsData = orderService.getOrderDetail(orderId);
        if (rsData.isFail()) {
            return rq.historyBack(rsData);
        }
        model.addAttribute("orderResponse", OrderResponse.of(rsData.getData()));

        return "usr/order/detail";
    }

    @RequestMapping("/{id}/success")
    public String confirmPayment(
            @PathVariable long id,
            @RequestParam String paymentKey,
            @RequestParam String orderId,
            @RequestParam Long amount,
            @RequestParam("addressId") String addressId,
            Model model) throws Exception {

        Order order = orderService.findById(id).get();

        long orderIdInputed = Long.parseLong(orderId.split("_")[0]);

        if (id != orderIdInputed) {
            return rq.historyBack("주문번호가 일치하지 않습니다.");
        }

        if (!amount.equals(order.getTotalPrice())) {
            return rq.historyBack("주문 가격과 결제 가격이 일치하지 않습니다.");
        }

        for(OrderItem orderItem : order.getOrderItems()){
            if(orderItem.getProduct().getStock() < orderItem.getQuantity()){
                return rq.historyBack("%s의 주문 수량이 재고보다 많습니다.".formatted(orderItem.getProduct().getName()));
            }
        }

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Basic " + Base64.getEncoder().encodeToString((AppConfig.getTossPaymentSecretKey() + ":").getBytes()));
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, String> payloadMap = new HashMap<>();
        payloadMap.put("orderId", orderId);
        payloadMap.put("amount", String.valueOf(amount));

        HttpEntity<String> request = new HttpEntity<>(objectMapper.writeValueAsString(payloadMap), headers);

        ResponseEntity<JsonNode> responseEntity = restTemplate.postForEntity(
                "https://api.tosspayments.com/v1/payments/" + paymentKey, request, JsonNode.class);

        if (responseEntity.getStatusCode() == HttpStatus.OK) {

            orderService.payByTossPayments(order, orderId, Long.parseLong(addressId));
            extractMessageFromResponse(responseEntity, order);

            return rq.redirectWithMsg("/order/detail/%s".formatted(order.getId()), "주문이 완료되었습니다.");
        } else {
            JsonNode failNode = responseEntity.getBody();
            model.addAttribute("orderNumber", orderId);
            model.addAttribute("message", failNode.get("message").asText());
            model.addAttribute("code", failNode.get("code").asText());
            return rq.historyBack("결제 실패");
        }
    }

    private void extractMessageFromResponse(ResponseEntity<JsonNode> responseEntity, Order order) {
        JsonNode body = responseEntity.getBody();

        String requestedAt = body.get("requestedAt").asText();
        LocalDateTime payDate = LocalDateTime.parse(requestedAt, DateTimeFormatter.ISO_OFFSET_DATE_TIME);

        String method = body.get("method").asText();
        Long totalAmount = body.get("totalAmount").asLong();
        String status = body.get("status").asText();

        orderService.updatePayment(order, method, totalAmount, payDate, status);
    }

    @RequestMapping("/{id}/fail")
    public String failPayment(@RequestParam String message, @RequestParam String code, @RequestParam String orderNumber, Model model) {
        model.addAttribute("orderNumber", orderNumber);
        model.addAttribute("message", message);
        model.addAttribute("code", code);
        return "order/fail";
    }


    @GetMapping("/management/{orderId}")
    @PreAuthorize("hasAuthority('SELLER')")
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

    @PostMapping("/update/status/{orderItemId}")
    @PreAuthorize("hasAuthority('SELLER')")
    public String updateOrderStatus(@PathVariable Long orderItemId,
                                    @RequestParam Long orderId,
                                    @RequestParam String newStatus,
                                    Model model) {

        RsData<OrderItem> updateRsData = orderService.updateOrderStatus(orderItemId, newStatus);
        if (updateRsData.isFail()) {
            rq.historyBack(updateRsData);
        }
        return rq.redirectWithMsg("/order/management/%s".formatted(orderId), updateRsData);
    }

}
