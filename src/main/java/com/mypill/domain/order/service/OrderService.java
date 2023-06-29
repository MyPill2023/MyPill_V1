package com.mypill.domain.order.service;

import com.mypill.domain.cart.entity.CartProduct;
import com.mypill.domain.cart.service.CartService;
import com.mypill.domain.member.entity.Member;
import com.mypill.domain.order.dto.response.OrderResponse;
import com.mypill.domain.order.entity.Order;
import com.mypill.domain.order.entity.OrderItem;
import com.mypill.domain.order.repository.OrderItemRepository;
import com.mypill.domain.order.repository.OrderRepository;
import com.mypill.global.rsData.RsData;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional
    public RsData<Order> createFromCart(Member member) {
        List<CartProduct> cartProducts = cartService.findByMemberId(member.getId()).getCartProducts();

        for(CartProduct cartProduct : cartProducts){
            if(cartProduct.getQuantity() > cartProduct.getProduct().getStock()){
                return RsData.of("F-1", "%s의 수량이 현재 재고보다 많습니다.".formatted(cartProduct.getProduct().getName()));
            }
        }

        List<OrderItem> orderItems = cartProducts.stream()
                .filter(cartProduct -> cartProduct.getDeleteDate() == null)
                .map(cartProduct -> new OrderItem(cartProduct.getProduct(), cartProduct.getQuantity()))
                .toList();

        Order order = create(member, orderItems);
        return RsData.of("S-1", "주문이 생성되었습니다.", order);
    }

    @Transactional
    public Order create(Member member, List<OrderItem> orderItems) {

        Order order = new Order(member);

        for (OrderItem orderItem : orderItems) {
            order.addOrderItem(orderItem);
            orderItem.connectOrder(order);
        }

        order.makeName();

        orderRepository.save(order);

        return order;
    }

    public Optional<Order> findById(Long orderId) {
        return orderRepository.findById(orderId);
    }
}
