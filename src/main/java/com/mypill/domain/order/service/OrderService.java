package com.mypill.domain.order.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mypill.domain.address.entity.Address;
import com.mypill.domain.address.service.AddressService;
import com.mypill.domain.cart.entity.CartProduct;
import com.mypill.domain.cart.service.CartService;
import com.mypill.domain.member.entity.Member;
import com.mypill.domain.member.entity.Role;
import com.mypill.domain.order.dto.request.PayRequest;
import com.mypill.domain.order.dto.response.OrderListResponse;
import com.mypill.domain.order.dto.response.PayResponse;
import com.mypill.domain.order.entity.Order;
import com.mypill.domain.order.entity.OrderItem;
import com.mypill.domain.order.entity.OrderStatus;
import com.mypill.domain.order.repository.OrderItemRepository;
import com.mypill.domain.order.repository.OrderRepository;
import com.mypill.domain.product.entity.Product;
import com.mypill.domain.product.service.ProductService;
import com.mypill.global.AppConfig;
import com.mypill.global.event.EventAfterOrderChanged;
import com.mypill.global.event.EventAfterOrderStatusUpdate;
import com.mypill.global.rq.Rq;
import com.mypill.global.rsdata.RsData;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final CartService cartService;
    private final AddressService addressService;
    private final ProductService productService;
    private final Rq rq;
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper;
    private final ApplicationEventPublisher publisher;

    @Transactional(readOnly = true)
    public RsData<Order> getOrderForm(Member buyer, Long orderId) {
        Order order = findById(orderId).orElse(null);
        if (order == null) {
            return RsData.of("F-1", "존재하지 않는 주문입니다.");
        }
        if (!Objects.equals(order.getBuyer().getId(), buyer.getId())) {
            return RsData.of("F-2", "다른 회원의 주문에 접근할 수 없습니다.");
        }
        if (order.getPayment() != null) {
            return RsData.of("F-3", "이미 결제된 주문입니다.");
        }
        return RsData.of("S-1", order);
    }

    @Transactional
    public RsData<Order> createFromCart(Member buyer) {
        List<CartProduct> cartProducts = cartService.getOrCreateCart(buyer).getCartProducts();
        return createAndConnect(buyer, cartProducts);
    }

    @Transactional
    public RsData<Order> createFromSelectedCartProduct(Member buyer, String[] selectedCartProductIds) {
        List<Long> ids = Arrays.stream(selectedCartProductIds).map(Long::valueOf).toList();
        List<CartProduct> cartProducts = cartService.findCartProductByIdIn(ids);
        return createAndConnect(buyer, cartProducts);
    }

    @Transactional
    public RsData<Order> createSingleProduct(Member buyer, Long productId, Long quantity) {
        Product product = productService.findById(productId).orElse(null);
        if (product == null) {
            return RsData.of("F-1", "존재하지 않는 상품입니다.");
        }
        OrderItem orderItem = new OrderItem(product, quantity);
        Order order = create(buyer, Collections.singletonList(orderItem));
        return RsData.of("S-1", "주문이 생성되었습니다.", order);
    }

    private RsData<Order> createAndConnect(Member buyer, List<CartProduct> cartProducts) {
        List<OrderItem> orderItems = createOrderItemsFromCartProducts(cartProducts);
        Order order = create(buyer, orderItems);
        cartProducts.forEach(order::addCartProduct);
        return RsData.of("S-1", "주문이 생성되었습니다.", order);
    }

    private Order create(Member buyer, List<OrderItem> orderItems) {
        Order order = new Order(buyer);
        orderItems.forEach(order::addOrderItem);
        order.makeName();
        return orderRepository.save(order);
    }

    @Transactional
    public void setOrderAsPaymentDone(Order order, String orderNumber, Long addressId) {
        Address address = addressService.findById(addressId).orElse(null);
        order.setAddress(address);
        order.setPaymentDone(orderNumber);
        processAfterPaymentOrCancellation(order, OrderStatus.ORDERED, OrderItem::getQuantity);
        cartService.hardDeleteCartProducts(order.getCartProducts());
    }

    private void processAfterPaymentOrCancellation(Order order, OrderStatus orderStatus, Function<OrderItem, Long> quantityModifier) {
        Set<Member> uniqueSellers = new HashSet<>();
        order.getOrderItems()
                .forEach(orderItem -> {
                    orderItem.updateStatus(orderStatus);
                    productService.updateStockAndSalesByOrder(orderItem.getProduct().getId(), quantityModifier.apply(orderItem)); // 재고 업데이트
                    Member seller = orderItem.getProduct().getSeller();
                    if (uniqueSellers.add(seller)) {
                        publisher.publishEvent(new EventAfterOrderChanged(this, seller, order, orderStatus)); // 이벤트 - 판매자에게 알림
                    }
                });
        order.updatePrimaryOrderStatus(orderStatus);
    }

    @Transactional
    public void updatePayment(Order order, String paymentKey, String method, Long totalAmount, LocalDateTime payDate, String status) {
        order.updatePayment(paymentKey, method, totalAmount, payDate, status);
        orderRepository.save(order);
    }

    @Transactional(readOnly = true)
    public RsData<Order> getOrderDetails(Member member, Long orderId) {
        Order order = findById(orderId).orElse(null);
        if (!isOrderValidAndDonePayment(order)) {
            return RsData.of("F-1", "존재하지 않는 주문입니다.");
        }
        if (!isOrderAccessibleByBuyer(order, member)) {
            return RsData.of("F-2", "접근 권한이 없습니다.");
        }
        return RsData.of("S-1", order);
    }

    @Transactional(readOnly = true)
    public RsData<Order> checkIfOrderCanBeCancelled(Member buyer, Long orderId) {
        Order order = findByIdAndPaymentIsNotNull(orderId).orElse(null);
        if (order == null) {
            return RsData.of("F-1", "존재하지 않는 주문입니다.");
        }
        if (!Objects.equals(order.getBuyer().getId(), buyer.getId())) {
            return RsData.of("F-2", "접근 권한이 없습니다.");
        }
        if (order.getPayment().getStatus().equals("CANCELED")) {
            return RsData.of("F-3", "이미 취소된 주문입니다.");
        }
        for (OrderItem orderItem : order.getOrderItems()) {
            if (!orderItem.getStatus().equals(OrderStatus.ORDERED)) {
                return RsData.of("F-4", "%s인 상품이 있어 </br>주문 취소가 불가합니다".formatted(orderItem.getStatus().getValue()));
            }
        }
        return RsData.of("S-1", "취소 가능한 주문입니다.", order);
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
                String approvedAt = responseEntity.getBody().get("approvedAt").asText();
                LocalDateTime cancelDate = LocalDateTime.parse(approvedAt, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
                String status = responseEntity.getBody().get("status").asText();

                order.cancelPayment(cancelDate, status);
                processAfterPaymentOrCancellation(order, OrderStatus.CANCELED, orderItem -> -orderItem.getQuantity());
                orderRepository.save(order);
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

    @Transactional
    public RsData<OrderItem> updateOrderStatus(Long orderItemId, String newStatus) {
        OrderItem orderItem = findOrderItemById(orderItemId).orElse(null);
        if (orderItem == null) {
            return RsData.of("F-1", "존재하지 않는 주문 상품입니다.");
        }
        OrderStatus status = OrderStatus.findByValue(newStatus);
        if (status == null) {
            return RsData.of("F-2", "유효하지 않은 주문 상태입니다.");
        }
        orderItem.updateStatus(status);
        updatePrimaryOrderStatus(orderItem.getOrder());
        publisher.publishEvent(new EventAfterOrderStatusUpdate(this, orderItem.getOrder().getBuyer(), orderItem, status));
        return RsData.of("S-1", "주문 상태가 변경되었습니다.", orderItem);
    }

    @Transactional
    public void updatePrimaryOrderStatus(Order order) {
        order.updatePrimaryOrderStatus(getPrimaryOrderItemStatus(order));
    }

    public OrderStatus getPrimaryOrderItemStatus(Order order) {
        List<OrderItem> orderItems = order.getOrderItems();
        if (orderItems.isEmpty()) {
            return null;
        }
        return orderItems.stream()
                .map(OrderItem::getStatus)
                .min(Comparator.comparing(OrderStatus::getNumber))
                .orElse(null);
    }

    @Transactional(readOnly = true)
    public RsData<Order> validateOrder(PayRequest payRequest) {
        long orderId = Long.parseLong(payRequest.getOrderId().split("_")[0]);
        Order order = findById(orderId).orElse(null);
        if (order == null) {
            return RsData.of("F-1", "존재하지 않는 주문입니다.");
        }
        if (!payRequest.getAmount().equals(order.getTotalPrice())) {
            return RsData.of("F-2", "주문 가격과 결제 가격이 일치하지 않습니다.");
        }
        for (OrderItem orderItem : order.getOrderItems()) {
            if (orderItem.getProduct().getStock() < orderItem.getQuantity()) {
                return RsData.of("F-", "%s의 주문 수량이 재고보다 많습니다.".formatted(orderItem.getProduct().getName()));
            }
        }
        return RsData.of("S-1", "결제 가능합니다.", order);
    }

    public Optional<Order> findById(Long orderId) {
        return orderRepository.findById(orderId);
    }

    public Optional<OrderItem> findOrderItemById(Long id) {
        return orderItemRepository.findById(id);
    }

    public List<Order> findByBuyerIdAndPaymentIsNotNull(Long buyerId) {
        return orderRepository.findByBuyerIdAndPaymentIsNotNull(buyerId);
    }

    public Optional<Order> findByIdAndPaymentIsNotNull(Long orderId) {
        return orderRepository.findByIdAndPaymentIsNotNull(orderId);
    }

    public List<Order> findBySellerId(Long sellerId) {
        return orderRepository.findBySellerId(sellerId);
    }

    public List<OrderItem> findOrderItemBySellerId(Long sellerId) {
        return orderItemRepository.findBySellerId(sellerId);
    }

    public List<OrderItem> findOrderItemByBuyerId(Long buyerId) {
        return orderItemRepository.findByBuyerId(buyerId);
    }

    private boolean isOrderValidAndDonePayment(Order order) {
        return order != null && order.getPayment() != null;
    }

    private boolean isOrderAccessibleByBuyer(Order order, Member member) {
        return !member.getRole().equals(Role.BUYER) || order.getBuyer().getId().equals(member.getId());
    }

    private List<OrderItem> createOrderItemsFromCartProducts(List<CartProduct> cartProducts) {
        return cartProducts.stream()
                .map(cartProduct -> new OrderItem(cartProduct.getProduct(), cartProduct.getQuantity()))
                .toList();
    }

    public Map<YearMonth, Long> countOrderPrice(Long sellerId) {
        List<OrderItem> orderItems = findOrderItemBySellerId(sellerId);
        return SalesCalculator.calculateMonthlySales(orderItems);
    }

    public List<OrderListResponse> getOrderListResponses(Long memberId) {
        List<Order> orders = findByBuyerIdAndPaymentIsNotNull(memberId);
        return orders.stream()
                .sorted(Comparator.comparing((Order order) -> order.getPayment().getPayDate()).reversed())
                .map(OrderListResponse::of).toList();
    }

    public Map<OrderStatus, Long> getOrderStatusCount(Long memberId) {
        List<OrderItem> orderItems = findOrderItemByBuyerId(memberId);
        return orderItems.stream()
                .collect(Collectors.groupingBy(OrderItem::getStatus, Collectors.counting()));
    }

    public OrderStatus[] getFilteredOrderStatus() {
        return Arrays.stream(OrderStatus.values())
                .filter(status -> status.getNumber() >= 1 && status.getNumber() <= 4)
                .toArray(OrderStatus[]::new);
    }

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
                setOrderAsPaymentDone(order, payRequest.getOrderId(), Long.parseLong(payRequest.getAddressId()));
                extractMessageFromResponse(responseEntity, order);
                return RsData.of("S-1", "주문이 완료되었습니다.", order);
            } else {
                JsonNode failNode = responseEntity.getBody();
                return RsData.of("F-2", "결제 실패", PayResponse.of(payRequest, failNode));
            }
        } catch (Exception e) {
            return RsData.of("F-1", "API 호출 오류", PayResponse.of(payRequest, "API 호출 오류", "F-1"));
        }
    }

    private void extractMessageFromResponse(ResponseEntity<JsonNode> responseEntity, Order order) {
        JsonNode body = responseEntity.getBody();
        String requestedAt = body.get("requestedAt").asText();
        LocalDateTime payDate = LocalDateTime.parse(requestedAt, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        String paymentKey = body.get("paymentKey").asText();
        String method = body.get("method").asText();
        Long totalAmount = body.get("totalAmount").asLong();
        String status = body.get("status").asText();
        updatePayment(order, paymentKey, method, totalAmount, payDate, status);
    }

    public List<OrderItem> findByProductSellerIdAndOrderId(Long sellerId, Long orderId) {
        return orderItemRepository.findByProductSellerIdAndOrderId(sellerId, orderId);
    }

    public static class SalesCalculator {
        public static Map<YearMonth, Long> calculateMonthlySales(List<OrderItem> orderItems) {
            return orderItems.stream()
                    .filter(orderItem -> orderItem.getStatus().equals(OrderStatus.DELIVERED))
                    .collect(Collectors.groupingBy(
                            orderItem -> YearMonth.from(orderItem.getCreateDate()),
                            Collectors.summingLong(OrderItem::getTotalPrice)
                    ));
        }
    }
}
