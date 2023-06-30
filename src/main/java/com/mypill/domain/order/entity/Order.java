package com.mypill.domain.order.entity;

import com.mypill.domain.cart.entity.CartProduct;
import com.mypill.domain.member.entity.Member;
import com.mypill.global.AppConfig;
import com.mypill.global.base.entitiy.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "orders")
public class Order extends BaseEntity {

    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    @Builder.Default
    private List<CartProduct> cartProducts = new ArrayList<>();

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    @Builder.Default
    private List<OrderItem> orderItems = new ArrayList<>();

    @Column(nullable = false)
    private Long totalPrice;

    private boolean isPaid;


    public Order(Member member) {
        this.member = member;
        this.cartProducts = new ArrayList<>();
        this.orderItems = new ArrayList<>();
        this.totalPrice = 0L;
    }

    public void addOrderItem(OrderItem orderItem){
        this.orderItems.add(orderItem);
        this.totalPrice += orderItem.getTotalPrice();
    }

    public void makeName() {
        StringBuilder sb = new StringBuilder();
        String productName = orderItems.get(0).getProduct().getName();
        int maxOrderNameLength = AppConfig.getMaxOrderNameLength();

        if (productName.length() > maxOrderNameLength) {
            sb.append(productName.substring(0, maxOrderNameLength + 1));
            sb.append("...");
        } else {
            sb.append(productName);
        }

        if (orderItems.size() > 1) {
            sb.append(" 외 %d개".formatted(orderItems.size() - 1));
        }

        this.name = sb.toString();
    }

    public void setPaymentDone() {
        for (OrderItem orderItem : orderItems) {
            orderItem.setPaymentDone();
        }

        isPaid = true;
    }
}
