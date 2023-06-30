package com.mypill.domain.order.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mypill.domain.member.entity.Member;
import com.mypill.domain.order.dto.response.OrderResponse;
import com.mypill.domain.order.entity.Order;
import com.mypill.domain.order.service.OrderService;
import com.mypill.global.AppConfig;
import com.mypill.global.rq.Rq;
import com.mypill.global.rsData.RsData;
import com.mypill.global.util.Ut;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import static javax.crypto.Cipher.SECRET_KEY;


@Controller
@RequiredArgsConstructor
@RequestMapping("/order")
public class OrderController {

    private final OrderService orderService;
    private final Rq rq;
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper;

    @GetMapping("/form/{orderId}")
    @PreAuthorize("hasAuthority('MEMBER')")
    public String showOrderForm(@PathVariable Long orderId, Model model) {
        RsData<OrderResponse> rsData = orderService.showOrderForm(orderId);
        if(rsData.isFail()){
            return rq.historyBack(rsData);
        }
        model.addAttribute("orderResponse", rsData.getData());
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
    public String showOrder(@PathVariable Long orderId, Model model) {

        Order order = orderService.findById(orderId).orElse(null);

        if(order == null){
            rq.historyBack("비었어요");
        }

        model.addAttribute("orderResponse", OrderResponse.of(order));

        return "usr/order/detail";
    }

    @RequestMapping("/{id}/success")
    public String confirmPayment(
            @PathVariable long id,
            @RequestParam String paymentKey,
            @RequestParam String orderId,
            @RequestParam Long amount,
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

            orderService.payByTossPayments(order, payDate, orderId);

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
