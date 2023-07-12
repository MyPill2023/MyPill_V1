package com.mypill.domain.buyer.controller;

import com.mypill.domain.address.dto.response.AddressResponse;
import com.mypill.domain.address.entity.Address;
import com.mypill.domain.address.service.AddressService;
import com.mypill.domain.member.entity.Member;
import com.mypill.domain.nutrient.entity.Nutrient;
import com.mypill.domain.order.dto.response.OrderListResponse;
import com.mypill.domain.order.entity.OrderStatus;
import com.mypill.domain.order.service.OrderService;
import com.mypill.global.rq.Rq;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/buyer")
@Tag(name = "BuyerController", description = "구매자 회원")
public class BuyerController {
    private final OrderService orderService;
    private final AddressService addressService;
    private final Rq rq;

    @PreAuthorize("hasAuthority('BUYER')")
    @GetMapping("/myLikes")
    @Operation(summary = "내 관심상품 페이지")
    public String myLikes() {
        return "usr/buyer/myLikes";
    }

    @PreAuthorize("hasAuthority('BUYER')")
    @GetMapping("/mySchedule")
    @Operation(summary = "내 복약관리 페이지")
    public String mySchedule() {
        return "usr/buyer/mySchedule";
    }

    @PreAuthorize("hasAuthority('BUYER')")
    @GetMapping("/myOrder")
    @Operation(summary = "내 주문 페이지")
    public String myOrder(Model model) {
        List<OrderListResponse> orderListResponses = orderService.getOrderListResponses(rq.getMember().getId());
        model.addAttribute("orders", orderListResponses);
        Map<OrderStatus, Long> orderStatusCount = orderService.getOrderStatusCount(rq.getMember().getId());
        model.addAttribute("orderStatusCount", orderStatusCount);
        OrderStatus[] filteredOrderStatus = orderService.getFilteredOrderStatus();
        model.addAttribute("orderStatuses", filteredOrderStatus);
        return "usr/buyer/myOrder";
    }

    @PreAuthorize("hasAuthority('BUYER')")
    @GetMapping("/myAddress")
    @Operation(summary = "내 배송지 페이지")
    public String myAddress(Model model) {
        List<Address> addresses = addressService.findByMemberId(rq.getMember().getId());
        List<AddressResponse> addressResponses = addresses.stream()
                .filter(address -> address.getDeleteDate() == null)
                .map(AddressResponse::of).toList();
        model.addAttribute("addresses", addressResponses);
        return "usr/buyer/myAddress";
    }

    @PreAuthorize("hasAuthority('BUYER')")
    @GetMapping("/mySurvey")
    public String mySurvey(Model model) {
        Member member = rq.getMember();
        List<Nutrient> nutrients = member.getSurveyNutrients();
        model.addAttribute("nutrients", nutrients);
        return "usr/buyer/mySurvey";
    }
}