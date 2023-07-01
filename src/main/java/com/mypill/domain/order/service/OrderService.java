package com.mypill.domain.order.service;

import com.mypill.domain.address.dto.request.AddressRequest;
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
import com.mypill.domain.product.Service.ProductService;
import com.mypill.domain.product.entity.Product;
import com.mypill.global.rq.Rq;
import com.mypill.global.rsData.RsData;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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

    public RsData<OrderResponse> getOrderForm(Long orderId) {
        Order order = findById(orderId).orElse(null);
        if(order == null){
            return RsData.of("F-1", "존재하지 않는 주문입니다.");
        }
        if(!order.getBuyer().getId().equals(rq.getMember().getId())){
            return RsData.of("F-2", "다른 회원의 주문에 접근할 수 없습니다.");
        }
        if(order.getPayDate() != null){
            return RsData.of("F-3", "이미 결제된 주문입니다.");
        }

        return  RsData.of("S-1", OrderResponse.of(order));
    }

    @Transactional
    public RsData<Order> createFromCart(Member buyer) {
        List<CartProduct> cartProducts = cartService.findByMemberId(buyer.getId()).getCartProducts();

        for(CartProduct cartProduct : cartProducts){
            if(cartProduct.getQuantity() > cartProduct.getProduct().getStock()){
                return RsData.of("F-1", "%s의 수량이 현재 재고보다 많습니다.".formatted(cartProduct.getProduct().getName()));
            }
        }

        List<OrderItem> orderItems = cartProducts.stream()
                .filter(cartProduct -> cartProduct.getDeleteDate() == null)
                .map(cartProduct -> new OrderItem(cartProduct.getProduct(), cartProduct.getQuantity()))
                .toList();

        Order order = create(buyer, orderItems);
        for(CartProduct cartProduct : cartProducts){
            order.addCartProduct(cartProduct);
        }
        return RsData.of("S-1", "주문이 생성되었습니다.", order);
    }


    // 임시 NotProd용
    @Transactional
    public RsData<Order> createFromProduct(Member buyer, Long productId, Long quantity) {
        Product product = productService.findById(productId).orElse(null);

        List<OrderItem> orderItems = new ArrayList<>();
        orderItems.add(new OrderItem(product, quantity));

        Order order = create(buyer, orderItems);
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
        orderRepository.save(order);

        return order;
    }

    @Transactional
    public void payByTossPayments(Order order, LocalDateTime payDate, String orderId, AddressRequest addressRequest) {

        order.setPaymentDone(payDate, orderId);
        Address address = addressService.addAddress(addressRequest);
        order.addAddress(address);
        order.getOrderItems()
                .forEach(orderItem -> {
                    orderItem.updateStatus(OrderStatus.ORDERED);
                    orderItem.getProduct().updateStockByOrder(orderItem.getQuantity()); // 재고 업데이트
                });
        order.getCartProducts().forEach(CartProduct::softDelete); // 장바구니에서 삭제

        orderRepository.save(order);
    }

    public RsData<OrderResponse> getOrderDetail(Long orderId) {
        Order order = findById(orderId).orElse(null);
        if(order == null || order.getPayDate() == null){
            return RsData.of("F-1", "존재하지 않는 주문입니다.");
        }
        if(!order.getBuyer().getId().equals(rq.getMember().getId())){
            return RsData.of("F-2", "다른 회원의 주문에 접근할 수 없습니다.");
        }

        return  RsData.of("S-1", OrderResponse.of(order));
    }

    public Optional<Order> findById(Long orderId) {
        return orderRepository.findById(orderId);
    }
    public List<Order> findByBuyerId(Long buyerId) {
        return orderRepository.findByBuyerId(buyerId);
    }
}
