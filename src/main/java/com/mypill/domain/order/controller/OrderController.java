package com.mypill.domain.order.controller;

import com.mypill.domain.member.entity.Member;
import com.mypill.domain.order.dto.response.OrderResponse;
import com.mypill.domain.order.entity.Order;
import com.mypill.domain.order.service.OrderService;
import com.mypill.global.rq.Rq;
import com.mypill.global.rsData.RsData;
import com.mypill.global.util.Ut;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/order")
public class OrderController {

    private final OrderService orderService;
    private final Rq rq;


    @GetMapping("/form/{orderId}")
    @PreAuthorize("hasAuthority('MEMBER')")
    public String orderForm(@PathVariable Long orderId, Model model) {

        Order order = orderService.findById(orderId).orElse(null);

        if(order == null){
            rq.historyBack("비었어요");
        }

        model.addAttribute("orderResponse", OrderResponse.of(order));

        return "usr/order/form";
    }

    @PostMapping("/makeOrder")
    @PreAuthorize("hasAuthority('MEMBER')")
    public String makeOrder() {
        Member member = rq.getMember();
        RsData<Order> orderRsData = orderService.createFromCart(member);

        if(orderRsData.isFail()){
            return rq.historyBack(orderRsData);
        }

//        return rq.redirectWithMsg("/cart", orderRsData);
        return rq.redirectWithMsg("/order/form/%s".formatted(orderRsData.getData().getId()), orderRsData);
    }



}
