package com.mypill.domain.order.service;

import com.mypill.domain.address.entity.Address;
import com.mypill.domain.address.service.AddressService;
import com.mypill.domain.cart.entity.CartProduct;
import com.mypill.domain.cart.service.CartService;
import com.mypill.domain.member.entity.Member;
import com.mypill.domain.order.dto.response.OrderResponse;
import com.mypill.domain.order.entity.Order;
import com.mypill.domain.order.entity.OrderItem;
import com.mypill.domain.order.entity.OrderStatus;
import com.mypill.domain.order.repository.OrderItemRepository;
import com.mypill.domain.order.repository.OrderRepository;
import com.mypill.domain.product.entity.Product;
import com.mypill.domain.product.service.ProductService;
import com.mypill.global.rq.Rq;
import com.mypill.global.rsData.RsData;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;

    private final OrderItemRepository orderItemRepository;
    private final CartService cartService;
    private final AddressService addressService;
    private final ProductService productService;
    private final Rq rq;

    public RsData<OrderResponse> getOrderForm(Long orderId) {
        Order order = findById(orderId).orElse(null);
        if(order == null){
            return RsData.of("F-1", "존재하지 않는 주문입니다.");
        }
        if(!order.getBuyer().getId().equals(rq.getMember().getId())){
            return RsData.of("F-2", "다른 회원의 주문에 접근할 수 없습니다.");
        }
        if(order.getPayment() != null){
            return RsData.of("F-3", "이미 결제된 주문입니다.");
        }

        return  RsData.of("S-1", OrderResponse.of(order));
    }

    @Transactional
    public RsData<Order> createFromCart(Member buyer) {
        List<CartProduct> cartProducts = cartService.findByMemberId(buyer.getId()).getCartProducts();
        return createAndConnect(buyer, cartProducts);
    }

    @Transactional
    public RsData<Order> createFromSelectedCartProduct(Member buyer, String[] selectedCartProductIds) {
        List<Long> ids = Arrays.stream(selectedCartProductIds).map(Long::valueOf).toList();
        List<CartProduct> cartProducts = cartService.findCartProductByIdIn(ids);
        return createAndConnect(buyer, cartProducts);
    }

    // 임시 NotProd용
    @Transactional
    public RsData<Order> createFromProduct(Member buyer, Long productId, Long quantity) {
        Product product = productService.findById(productId).orElseThrow();

        List<OrderItem> orderItems = new ArrayList<>();
        orderItems.add(new OrderItem(product, quantity));

        Order order = create(buyer, orderItems);
        return RsData.of("S-1", "주문이 생성되었습니다.", order);
    }

    @Transactional
    public RsData<Order> createAndConnect(Member buyer, List<CartProduct> cartProducts){
        List<OrderItem> orderItems = createOrderItemsFromCartProducts(cartProducts);
        Order order = create(buyer, orderItems);
        addCartProductsToOrder(order, cartProducts);
        return RsData.of("S-1", "주문이 생성되었습니다.", order);
    }

    @Transactional
    public Order create(Member buyer, List<OrderItem> orderItems) {

        Order order = new Order(buyer);

        for (OrderItem orderItem : orderItems) {
            order.addOrderItem(orderItem);
            orderItem.updateStatus(OrderStatus.BEFORE);
        }

        order.makeName();
        order.updatePrimaryOrderStatus(OrderStatus.BEFORE);
        orderRepository.save(order);

        return order;
    }

    @Transactional
    public void payByTossPayments(Order order, String orderId, Long addressId) {

        order.setPaymentDone(orderId);
        Address address = addressService.findById(addressId).orElse(null);
        order.addAddress(address);
        order.getOrderItems()
                .forEach(orderItem -> {
                    orderItem.updateStatus(OrderStatus.ORDERED);
                    orderItem.getProduct().updateStockByOrder(orderItem.getQuantity()); // 재고 업데이트
                });
        order.updatePrimaryOrderStatus(OrderStatus.ORDERED);
        order.getCartProducts().forEach(CartProduct::softDelete); // 장바구니에서 삭제

        orderRepository.save(order);
    }

    @Transactional
    public void updatePayment(Order order, String method, Long totalAmount, LocalDateTime payDate, String status){
        order.updatePayment(method, totalAmount, payDate, status);
        orderRepository.save(order);
    }

    // 결제 완료된 주문을 조회
    public RsData<Order> getOrderDetail(Long orderId) {
        Order order = findById(orderId).orElse(null);
        if (!isOrderValidAndDonePayment(order)) {
            return RsData.of("F-1", "존재하지 않는 주문입니다.");
        }

        Member member = rq.getMember();
        if (member.getUserType() == 1 && !isOrderAccessibleByBuyer(order, member)) {
            return RsData.of("F-2", "접근 권한이 없습니다.");
        }

        return RsData.of("S-1", order);
    }

    @Transactional
    public RsData<OrderItem> updateOrderStatus(Long orderItemId, String newStatus) {
        OrderItem orderItem = findOrderItemById(orderItemId).orElse(null);
        if(orderItem == null){
            return RsData.of("F-1", "존재하지 않는 주문 상품입니다.");
        }

        OrderStatus status = OrderStatus.findByValue(newStatus);
        if (status == null) {
            return RsData.of("F-2", "유효하지 않은 주문 상태입니다.");
        }

        orderItem.updateStatus(status);
        updatePrimaryOrderStatus(orderItem.getOrder());

        return RsData.of("S-1", "주문 상태가 변경되었습니다.");
    }

    @Transactional
    public void updatePrimaryOrderStatus(Order order){
        order.updatePrimaryOrderStatus(getHighestPriorityOrderItemStatus(order));
    }

    public OrderStatus getHighestPriorityOrderItemStatus(Order order){
        List<OrderItem> orderItems = order.getOrderItems();
        if (orderItems.isEmpty()) {
            return null;
        }
        return orderItems.stream()
                .map(OrderItem::getStatus)
                .min(Comparator.comparing(OrderStatus::getPriority))
                .orElse(null);
    }

    public RsData<Order> validateOrder(Long id, String orderId, Long amount) {

        Order order = findById(id).orElse(null);
        if (order == null) {
            return RsData.of("F-1", "존재하지 않는 주문입니다.");
        }

        long orderIdInputted = Long.parseLong(orderId.split("_")[0]);
        if (id != orderIdInputted) {
            return RsData.of("F-2","주문번호가 일치하지 않습니다.");
        }
        if (!amount.equals(order.getTotalPrice())) {
            return RsData.of("F-3", "주문 가격과 결제 가격이 일치하지 않습니다.");
        }

        for(OrderItem orderItem : order.getOrderItems()){
            if(orderItem.getProduct().getStock() < orderItem.getQuantity()){
                return RsData.of("F-4", "%s의 주문 수량이 재고보다 많습니다.".formatted(orderItem.getProduct().getName()));
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
    public List<Order> findByBuyerId(Long buyerId) {
        return orderRepository.findByBuyerId(buyerId);
    }
    public List<Order> findByBuyerIdAndPaymentIsNotNull(Long buyerId) {
        return orderRepository.findByBuyerIdAndPaymentIsNotNull(buyerId);
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
        return order.getBuyer().getId().equals(member.getId());
    }

    private List<OrderItem> createOrderItemsFromCartProducts(List<CartProduct> cartProducts) {
        return cartProducts.stream()
                .filter(cartProduct -> cartProduct.getDeleteDate() == null)
                .map(cartProduct -> new OrderItem(cartProduct.getProduct(), cartProduct.getQuantity()))
                .toList();
    }
    private void addCartProductsToOrder(Order order, List<CartProduct> cartProducts) {
        for (CartProduct cartProduct : cartProducts) {
            order.addCartProduct(cartProduct);
        }
    }
}
