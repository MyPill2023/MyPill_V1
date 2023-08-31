package com.mypill.domain.order.service;

import com.mypill.domain.address.entity.Address;
import com.mypill.domain.address.service.AddressService;
import com.mypill.domain.cart.entity.CartProduct;
import com.mypill.domain.cart.service.CartService;
import com.mypill.domain.member.entity.Member;
import com.mypill.domain.member.entity.Role;
import com.mypill.domain.order.dto.request.PayRequest;
import com.mypill.domain.order.entity.Order;
import com.mypill.domain.order.entity.OrderItem;
import com.mypill.domain.order.entity.OrderStatus;
import com.mypill.domain.order.entity.Payment;
import com.mypill.domain.order.repository.OrderItemRepository;
import com.mypill.domain.order.repository.OrderRepository;
import com.mypill.domain.product.entity.Product;
import com.mypill.domain.product.service.ProductService;
import com.mypill.global.event.EventAfterOrderChanged;
import com.mypill.global.event.EventAfterOrderStatusUpdate;
import com.mypill.global.rsdata.RsData;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.YearMonth;
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
    private final ApplicationEventPublisher publisher;

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
    public Map<OrderStatus, Long> getOrderStatusCount(List<OrderItem> orderItems) {
        return orderItems.stream()
                .collect(Collectors.groupingBy(OrderItem::getStatus, Collectors.counting()));
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
        return RsData.of("S-1", order);
    }

    @Transactional(readOnly = true)
    public RsData<Order> checkIfOrderCanBePaid(PayRequest payRequest) {
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
                return RsData.of("F-3", "%s의 /br> 주문 수량이  재고보다 많습니다.".formatted(orderItem.getProduct().getName()));
            }
        }
        return RsData.of("S-1", order);
    }

    @Transactional
    public void updateOrderAsPaymentDone(Order order, String orderNumber, Long addressId, Payment payment) {
        Address address = addressService.findById(addressId).orElse(null);
        order.setAddress(address);
        order.setPaymentDone(orderNumber);
        order.setPayment(payment);
        processAfterPaymentOrCancellation(order, OrderStatus.ORDERED, OrderItem::getQuantity);
        cartService.hardDeleteCartProductByOrderId(order.getId());
    }

    @Transactional
    public void updateOrderAsCancel(Order order, LocalDateTime cancelDate, String status) {
        order.cancelPayment(cancelDate, status);
        processAfterPaymentOrCancellation(order, OrderStatus.CANCELED, orderItem -> -orderItem.getQuantity());
    }

    @Transactional
    public RsData<OrderItem> updateOrderItemStatus(Long orderItemId, String newStatus) {
        OrderItem orderItem = findOrderItemById(orderItemId).orElse(null);
        if (orderItem == null) {
            return RsData.of("F-1", "존재하지 않는 주문 상품입니다.");
        }
        OrderStatus status = OrderStatus.findByValue(newStatus);
        if (status == null) {
            return RsData.of("F-2", "유효하지 않은 주문 상태입니다.");
        }
        orderItem.updateStatus(status);
        Order order = orderItem.getOrder();
        order.updatePrimaryOrderStatus(getPrimaryOrderItemStatus(order));

        publisher.publishEvent(new EventAfterOrderStatusUpdate(this, orderItem.getOrder().getBuyer(), orderItem, status));
        return RsData.of("S-1", "주문 상태가 변경되었습니다.", orderItem);
    }

    @Transactional(readOnly = true)
    public Map<YearMonth, Long> countOrderPrice(Long sellerId) {
        List<OrderItem> orderItems = findDeliveredOrderItemsBySellerId(sellerId);
        return orderItems.stream()
                .collect(Collectors.groupingBy(
                        orderItem -> YearMonth.from(orderItem.getCreateDate()),
                        Collectors.summingLong(OrderItem::getTotalPrice)
                ));
    }

    public Optional<Order> findById(Long orderId) {
        return orderRepository.findById(orderId);
    }

    public List<Order> findByBuyerId(Long buyerId) {
        return orderRepository.findByBuyerIdAndPaymentIsNotNullOrderByPayment_PayDateDesc(buyerId);
    }

    public List<Order> findBySellerId(Long sellerId) {
        return orderRepository.findBySellerId(sellerId);
    }

    public Optional<Order> findByIdAndPaymentIsNotNull(Long orderId) {
        return orderRepository.findByIdAndPaymentIsNotNull(orderId);
    }

    public Optional<OrderItem> findOrderItemById(Long id) {
        return orderItemRepository.findById(id);
    }

    public List<OrderItem> findOrderItemBySellerId(Long sellerId) {
        return orderItemRepository.findBySellerId(sellerId);
    }

    public List<OrderItem> findDeliveredOrderItemsBySellerId(Long sellerId) {
        return orderItemRepository.findDeliveredOrderItemsBySellerId(sellerId);
    }

    public List<OrderItem> findOrderItemByBuyerId(Long buyerId) {
        return orderItemRepository.findByBuyerId(buyerId);
    }

    public List<OrderItem> findByProductSellerIdAndOrderId(Long sellerId, Long orderId) {
        return orderItemRepository.findByProductSellerIdAndOrderId(sellerId, orderId);
    }

    private RsData<Order> createAndConnect(Member buyer, List<CartProduct> cartProducts) {
        List<OrderItem> orderItems = createOrderItemsFromCartProducts(cartProducts);
        Order order = create(buyer, orderItems);
        cartProducts.forEach(cartProduct -> cartProduct.connectOrderId(order.getId()));
        return RsData.of("S-1", "주문이 생성되었습니다.", order);
    }

    private Order create(Member buyer, List<OrderItem> orderItems) {
        Order order = new Order(buyer);
        orderItems.forEach(order::addOrderItem);
        order.makeName();
        return orderRepository.save(order);
    }

    private List<OrderItem> createOrderItemsFromCartProducts(List<CartProduct> cartProducts) {
        return cartProducts.stream()
                .map(cartProduct -> new OrderItem(cartProduct.getProduct(), cartProduct.getQuantity()))
                .toList();
    }

    private boolean isOrderValidAndDonePayment(Order order) {
        return order != null && order.getPayment() != null;
    }

    private boolean isOrderAccessibleByBuyer(Order order, Member member) {
        return !member.getRole().equals(Role.BUYER) || order.getBuyer().getId().equals(member.getId());
    }

    private void processAfterPaymentOrCancellation(Order order, OrderStatus orderStatus, Function<OrderItem, Long> quantityModifier) {
        Set<Member> uniqueSellers = new HashSet<>();
        order.getOrderItems()
                .forEach(orderItem -> {
                    orderItem.updateStatus(orderStatus); // orderItem 상태 업데이트
                    productService.updateStockAndSalesByOrder(orderItem.getProduct().getId(), quantityModifier.apply(orderItem)); // 재고 업데이트
                    Member seller = orderItem.getProduct().getSeller();
                    if (uniqueSellers.add(seller)) {
                        publisher.publishEvent(new EventAfterOrderChanged(this, seller, order, orderStatus)); // 이벤트 - 판매자에게 알림
                    }
                });
        order.updatePrimaryOrderStatus(orderStatus);
    }

    @Transactional
    public void hardDelete(LocalDateTime cutoffDate) {
        orderItemRepository.hardDelete(cutoffDate);
        orderRepository.hardDelete(cutoffDate);
    }
}
