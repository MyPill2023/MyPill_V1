package com.mypill.domain.buyer.controller;

import com.mypill.domain.address.dto.response.AddressResponse;
import com.mypill.domain.address.entity.Address;
import com.mypill.domain.address.service.AddressService;
import com.mypill.domain.member.entity.Member;
import com.mypill.domain.nutrient.service.NutrientService;
import com.mypill.domain.nutrient.entity.Nutrient;
import com.mypill.domain.order.dto.response.OrderListResponse;
import com.mypill.domain.order.entity.Order;
import com.mypill.domain.order.entity.OrderItem;
import com.mypill.domain.order.entity.OrderStatus;
import com.mypill.domain.order.service.OrderService;
import com.mypill.global.rq.Rq;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
@RequestMapping("/usr/buyer")
public class BuyerController {
    private final OrderService orderService;
    private final AddressService addressService;
    private final NutrientService nutrientService;
    private final Rq rq;

    @PreAuthorize("hasAuthority('BUYER')")
    @GetMapping("/myLikes")
    public String myLikes() {
        return "usr/buyer/myLikes";
    }


    @PreAuthorize("hasAuthority('BUYER')")
    @GetMapping("/mySchedule")
    public String mySchedule() {
        return "usr/buyer/mySchedule";
    }

    @PreAuthorize("hasAuthority('BUYER')")
    @GetMapping("/myOrder")
    public String myOrder(Model model) {

        List<Order> orders = orderService.findByBuyerIdAndPaymentIsNotNull(rq.getMember().getId());
        List<OrderListResponse> orderListResponses = orders.stream()
                .sorted(Comparator.comparing((Order order) -> order.getPayment().getPayDate()).reversed())
                .map(OrderListResponse::of).toList();
        model.addAttribute("orders", orderListResponses);

        List<OrderItem> orderItems = orderService.findOrderItemByBuyerId(rq.getMember().getId());
        Map<OrderStatus, Long> orderStatusCount = orderItems.stream()
                .collect(Collectors.groupingBy(OrderItem::getStatus, Collectors.counting()));

        model.addAttribute("orderStatusCount", orderStatusCount);

        OrderStatus[] filteredOrderStatus = Arrays.stream(OrderStatus.values())
                .filter(status -> status.getPriority() >=1 && status.getPriority() <= 4 )
                .toArray(OrderStatus[]::new);
        model.addAttribute("orderStatuses", filteredOrderStatus);

        return "usr/buyer/myOrder";
    }

    @PreAuthorize("hasAuthority('BUYER')")
    @GetMapping("/myAddress")
    public String myAddress(Model model) {

        List<Address> addresses = addressService.findByMemberId(rq.getMember().getId());
        List<AddressResponse> addressResponses = addresses.stream()
                .filter(address -> address.getDeleteDate() == null)
                .map(AddressResponse::of).toList();
        model.addAttribute("addresses",addressResponses);

        return "usr/buyer/myAddress";
    }

    @PreAuthorize("hasAuthority('BUYER')")
    @GetMapping("/mySurvey")
    public String mySurvey(Model model) {

        Member member = rq.getMember();

        List<Nutrient> nutrientAnswers = member.getSurveyNutrients();

        model.addAttribute("nutrientAnswers", nutrientAnswers);

        return "usr/buyer/mySurvey";
    }

}
