package com.mypill.domain.order.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mypill.domain.order.dto.request.PayRequest;
import com.mypill.domain.order.dto.response.PayResponse;
import com.mypill.domain.order.entity.Order;
import com.mypill.domain.order.entity.Payment;
import com.mypill.global.AppConfig;
import com.mypill.global.rsdata.RsData;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class TossPaymentService {
    private final OrderService orderService;
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper;

    @Transactional
    public RsData<?> pay(Order order, PayRequest payRequest) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Basic " + Base64.getEncoder().encodeToString((AppConfig.getTossPaymentSecretKey() + ":").getBytes()));
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, String> payloadMap = new HashMap<>();
            payloadMap.put("orderId", payRequest.getOrderId());
            payloadMap.put("amount", String.valueOf(payRequest.getAmount()));
            payloadMap.put("paymentKey", payRequest.getPaymentKey());

            HttpEntity<String> request = new HttpEntity<>(objectMapper.writeValueAsString(payloadMap), headers);

            ResponseEntity<JsonNode> responseEntity = restTemplate.postForEntity(
                    "https://api.tosspayments.com/v1/payments/confirm", request, JsonNode.class);
            if (responseEntity.getStatusCode() == HttpStatus.OK) {
                Payment payment = extractMessageFromResponse(responseEntity);
                orderService.updateOrderAsPaymentDone(order, payRequest.getOrderId(), Long.parseLong(payRequest.getAddressId()), payment);
                return RsData.of("S-1", "주문이 완료되었습니다.", order);
            } else {
                JsonNode failNode = responseEntity.getBody();
                return RsData.of("F-2", "결제 실패", PayResponse.of(payRequest, failNode));
            }
        } catch (Exception e) {
            return RsData.of("F-1", "API 호출 오류", PayResponse.of(payRequest, "API 호출 오류", "F-1"));
        }
    }

    @Transactional
    public RsData<?> cancel(Order order) {
        try {
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
                String approvedAt = responseEntity.getBody().get("cancels").get(0).get("canceledAt").asText();
                LocalDateTime cancelDate = LocalDateTime.parse(approvedAt, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
                String status = responseEntity.getBody().get("status").asText();

                orderService.updateOrderAsCancel(order, cancelDate, status);
                return RsData.of("S-1", "주문번호 %s의 </br> 주문 및 결제가 취소되었습니다.".formatted(order.getOrderNumber()), order);
            } else {
                JsonNode failNode = responseEntity.getBody();
                PayResponse payResponse = PayResponse.of(failNode);
                return RsData.of("F-2", "%s </br> %s".formatted(payResponse.getCode(), payResponse.getMessage()), PayResponse.of(failNode));
            }
        } catch (Exception e) {
            return RsData.of("F-1", "결제취소 실패");
        }
    }

    private Payment extractMessageFromResponse(ResponseEntity<JsonNode> responseEntity) {
        JsonNode body = responseEntity.getBody();
        String approvedAt = body.get("approvedAt").asText();
        LocalDateTime payDate = LocalDateTime.parse(approvedAt, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        String paymentKey = body.get("paymentKey").asText();
        String method = body.get("method").asText();
        Long totalAmount = body.get("totalAmount").asLong();
        String status = body.get("status").asText();
        return new Payment(paymentKey, method, totalAmount, payDate, status);
    }
}
