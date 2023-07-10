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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name="OrderController", description = "주문")
public class OrderController {

    private final OrderService orderService;
    private final AddressService addressService;
    private final Rq rq;
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper;

    @PreAuthorize("hasAuthority('BUYER')")
    @GetMapping("/form/{orderId}")
    @Operation(summary = "주문하기 폼")
    public String getOrderForm(@PathVariable Long orderId, Model model) {
        RsData<Order> rsData = orderService.getOrderForm(rq.getMember(), orderId);
        if (rsData.isFail()) {
            return rq.historyBack(rsData);
        }
        model.addAttribute("order", OrderResponse.of(rsData.getData()));

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

        if(selectedCartProductIds.length == 0){
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
    @Operation(summary = "구매자의 주문 내역 조회")
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
    public String confirmPayment(
            @PathVariable long id,
            @RequestParam String paymentKey,
            @RequestParam String orderId,
            @RequestParam Long amount,
            @RequestParam("addressId") String addressId,
            Model model) throws Exception {

        RsData<Order> validateRsData = orderService.validateOrder(id, orderId, amount);
        if(validateRsData.isFail()){
            return rq.historyBack(validateRsData);
        }

        Order order = validateRsData.getData();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Basic " + Base64.getEncoder().encodeToString((AppConfig.getTossPaymentSecretKey() + ":").getBytes()));
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, String> payloadMap = new HashMap<>();
        payloadMap.put("orderId", orderId);
        payloadMap.put("amount", String.valueOf(amount));
        payloadMap.put("paymentKey", paymentKey);

        HttpEntity<String> request = new HttpEntity<>(objectMapper.writeValueAsString(payloadMap), headers);

        ResponseEntity<JsonNode> responseEntity = restTemplate.postForEntity(
                "https://api.tosspayments.com/v1/payments/confirm", request, JsonNode.class);

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

    @PreAuthorize("hasAuthority('BUYER')")
    @GetMapping("/{id}/fail")
    @Operation(summary = "결제 실패")
    public String failPayment(@RequestParam String message, @RequestParam String code, @RequestParam String orderNumber, Model model) {
        model.addAttribute("orderNumber", orderNumber);
        model.addAttribute("message", message);
        model.addAttribute("code", code);
        return "order/fail";
    }

    @PreAuthorize("hasAuthority('BUYER')")
    @PostMapping("/cancel/{orderId}")
    @Operation(summary = "주문 취소")
    public String cancel(@PathVariable Long orderId) throws Exception {

        RsData<Order> checkRsData =  orderService.checkCanCancel(rq.getMember(), orderId);
        if(checkRsData.isFail()){
            return rq.historyBack(checkRsData);
        }

        Order order = checkRsData.getData();
        String paymentKey = order.getPayment().getPaymentKey();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Basic " + Base64.getEncoder().encodeToString((AppConfig.getTossPaymentSecretKey() + ":").getBytes()));
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, String> payloadMap = new HashMap<>();
        payloadMap.put("PaymentKey", order.getPayment().getPaymentKey());
        payloadMap.put("cancelReason", "");

        HttpEntity<String> request = new HttpEntity<>(objectMapper.writeValueAsString(payloadMap), headers);

        ResponseEntity<JsonNode> responseEntity = restTemplate.postForEntity(
                "https://api.tosspayments.com/v1/payments/%s/cancel".formatted(paymentKey), request, JsonNode.class);

        if (responseEntity.getStatusCode() == HttpStatus.OK) {

            String approvedAt = responseEntity.getBody().get("approvedAt").asText();
            LocalDateTime cancelDate = LocalDateTime.parse(approvedAt, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
            String status = responseEntity.getBody().get("status").asText();

            orderService.cancel(order, cancelDate, status);

            return rq.redirectWithMsg("/order/detail/%s".formatted(order.getId()), "주문번호 %s의 </br> 주문 및 결제가 취소되었습니다.".formatted(order.getOrderNumber()));
        } else {
            JsonNode failNode = responseEntity.getBody();
            String code = failNode.get("message").asText();
            String message = failNode.get("code").asText();
            return rq.historyBack("%s </br> %s".formatted(code, message));
        }
    }

    @PreAuthorize("hasAuthority('SELLER')")
    @GetMapping("/management/{orderId}")
    @Operation(summary = "판매자의 주문 관리")
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

    private void extractMessageFromResponse(ResponseEntity<JsonNode> responseEntity, Order order) {
        JsonNode body = responseEntity.getBody();

        String requestedAt = body.get("requestedAt").asText();
        LocalDateTime payDate = LocalDateTime.parse(requestedAt, DateTimeFormatter.ISO_OFFSET_DATE_TIME);

        String paymentKey = body.get("paymentKey").asText();
        String method = body.get("method").asText();
        Long totalAmount = body.get("totalAmount").asLong();
        String status = body.get("status").asText();

        orderService.updatePayment(order, paymentKey, method, totalAmount, payDate, status);
    }

}
