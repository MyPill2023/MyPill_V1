package com.mypill.domain.order.controller;

import com.mypill.domain.member.entity.Member;
import com.mypill.domain.order.entity.Order;
import com.mypill.domain.order.service.OrderService;
import com.mypill.global.rq.Rq;
import com.mypill.global.rsData.RsData;
import com.mypill.global.util.Ut;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/order")
public class OrderController {

    private final OrderService orderService;
    private final Rq rq;

    @PostMapping("/makeOrder")
    @PreAuthorize("hasAuthority('MEMBER')")
    public String makeOrder() {
        Member member = rq.getMember();
        RsData<Order> orderRsData = orderService.createFromCart(member);

        if(orderRsData.isFail()){
            return rq.historyBack(orderRsData);
        }

        return rq.redirectWithMsg("/order", orderRsData);
    }

}
