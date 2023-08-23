package com.mypill.domain.order.entity;

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
    private List<OrderItem> orderItems;
    @NotNull
    private Long totalPrice;
    @Embedded
    private Payment payment;
    @ManyToOne(fetch = FetchType.LAZY)
    private Address deliveryAddress;
    @Enumerated(EnumType.STRING)
    private OrderStatus primaryOrderStatus;

    public Order(Member buyer) {
        this.buyer = buyer;
        this.orderItems = new ArrayList<>();
        this.totalPrice = 0L;
        this.primaryOrderStatus = OrderStatus.BEFORE;
    }

    public void addOrderItem(OrderItem orderItem) {
        this.orderItems.add(orderItem);
        this.totalPrice += orderItem.getTotalPrice();
        orderItem.connectOrder(this);
    }

    public void makeName() {
        if (orderItems.isEmpty()) {
            this.name = "";
            return;
        }
        String productName = orderItems.get(0).getProduct().getName();
        StringBuilder sb = new StringBuilder(productName);
        int maxOrderNameLength = AppConfig.getMaxOrderNameLength();

        if (sb.length() > maxOrderNameLength) {
            sb.setLength(maxOrderNameLength + 1);
            sb.append("...");
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

    public void setPayment(Payment payment) {
        this.payment = payment;
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
