package com.mypill.domain.order.entity;

import com.mypill.domain.cart.entity.CartProduct;
import com.mypill.domain.address.entity.Address;
import com.mypill.domain.member.entity.Member;
import com.mypill.global.AppConfig;
import com.mypill.global.base.entity.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "orders")
public class Order extends BaseEntity {

    @Column(unique = true)
    private String orderNumber;
    private String name;
    @ManyToOne(fetch = FetchType.LAZY)
    private Member buyer;
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    @Builder.Default
    private List<CartProduct> cartProducts = new ArrayList<>();
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    @Builder.Default
    private List<OrderItem> orderItems = new ArrayList<>();
    @NotNull
    private Long totalPrice;
    @Embedded
    private Payment payment;
    @OneToOne(fetch = FetchType.LAZY)
    private Address deliveryAddress;
    private OrderStatus primaryOrderStatus;

    public Order(Member buyer) {
        this.buyer = buyer;
        this.cartProducts = new ArrayList<>();
        this.orderItems = new ArrayList<>();
        this.totalPrice = 0L;
    }

    public void addOrderItem(OrderItem orderItem) {
        this.orderItems.add(orderItem);
        this.totalPrice += orderItem.getTotalPrice();
        orderItem.connectOrder(this);
    }

    public void addCartProduct(CartProduct cartProduct) {
        cartProducts.add(cartProduct);
        cartProduct.connectOrder(this);
    }

    public void makeName() {
        StringBuilder sb = new StringBuilder();
        if (orderItems.isEmpty()) {
            this.name = "";
            return;
        }
        String productName = orderItems.get(0).getProduct().getName();
        int maxOrderNameLength = AppConfig.getMaxOrderNameLength();
        if (productName.length() > maxOrderNameLength) {
            sb.append(productName, 0, maxOrderNameLength + 1);
            sb.append("...");
        } else {
            sb.append(productName);
        }
        if (orderItems.size() > 1) {
            sb.append(" 외 %d 건".formatted(orderItems.size() - 1));
        }
        this.name = sb.toString();
    }

    public void setPaymentDone(String orderNumber) {
        for (OrderItem orderItem : orderItems) {
            orderItem.updateStatus(OrderStatus.ORDERED);
        }
        this.orderNumber = orderNumber;
    }

    public void updatePayment(String paymentKey, String method, Long totalAmount, LocalDateTime payDate, String status) {
        this.payment = new Payment(paymentKey, method, totalAmount, payDate, status);
    }

    public void cancelPayment(LocalDateTime cancelDate, String status) {
        this.payment.updateCancelData(cancelDate, status);
    }

    public void updatePrimaryOrderStatus(OrderStatus orderStatus) {
        this.primaryOrderStatus = orderStatus;
    }

    public void setAddress(Address address) {
        this.deliveryAddress = address;
    }
}
