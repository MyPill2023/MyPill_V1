package com.mypill.domain.order.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mypill.domain.address.dto.request.AddressRequest;
import com.mypill.domain.address.entity.Address;
import com.mypill.domain.address.service.AddressService;
import com.mypill.domain.member.entity.Member;
import com.mypill.domain.order.dto.response.OrderResponse;
import com.mypill.domain.order.entity.Order;
import com.mypill.domain.order.service.OrderService;
import com.mypill.global.AppConfig;
import com.mypill.global.rq.Rq;
import com.mypill.global.rsData.RsData;
import com.mypill.global.util.Ut;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriUtils;

import java.net.URLDecoder;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static javax.crypto.Cipher.SECRET_KEY;


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
        if(rsData.isFail()){
            return rq.historyBack(rsData);
        }
        model.addAttribute("orderResponse", rsData.getData());

        List<Address> addresses = addressService.findByMemberId(rq.getMember().getId());
        model.addAttribute("addresses", addresses);

        return "usr/order/form";
    }

    @PostMapping("/makeOrder")
    @PreAuthorize("hasAuthority('MEMBER')")
    public String makeOrder() {
        Member buyer = rq.getMember();
        RsData<Order> orderRsData = orderService.createFromCart(buyer);

        if(orderRsData.isFail()){
            return rq.historyBack(orderRsData);
        }

        return rq.redirectWithMsg("/order/form/%s".formatted(orderRsData.getData().getId()), orderRsData);
    }

    @GetMapping("/detail/{orderId}")
    @PreAuthorize("hasAuthority('MEMBER')")
    public String getOrderDetail(@PathVariable Long orderId, Model model) {

        RsData<OrderResponse> rsData = orderService.getOrderDetail(orderId);
        if(rsData.isFail()){
            return rq.historyBack(rsData);
        }
        model.addAttribute("orderResponse", rsData.getData());

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

        //TODO : 재고체크과정 필요?

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
            String requestedAt = responseEntity.getBody().get("requestedAt").asText();
            LocalDateTime payDate = LocalDateTime.parse(requestedAt, DateTimeFormatter.ISO_OFFSET_DATE_TIME);

            orderService.payByTossPayments(order, payDate, orderId, Long.parseLong(addressId));

            return rq.redirectWithMsg("/order/detail/%s".formatted(order.getId()), "주문이 완료되었습니다.");
        } else {
            JsonNode failNode = responseEntity.getBody();
            model.addAttribute("orderNumber", orderId);
            model.addAttribute("message", failNode.get("message").asText());
            model.addAttribute("code", failNode.get("code").asText());
            return rq.historyBack("결제 실패");
        }
    }

    @RequestMapping("/{id}/fail")
    public String failPayment(@RequestParam String message, @RequestParam String code, @RequestParam String orderNumber, Model model) {
        model.addAttribute("orderNumber", orderNumber);
        model.addAttribute("message", message);
        model.addAttribute("code", code);
        return "order/fail";
    }

}
