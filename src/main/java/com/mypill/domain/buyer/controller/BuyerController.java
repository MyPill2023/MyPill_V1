package com.mypill.domain.buyer.controller;

import com.mypill.domain.address.dto.response.AddressResponse;
import com.mypill.domain.address.entity.Address;
import com.mypill.domain.address.service.AddressService;
import com.mypill.domain.buyer.dto.response.MyOrderResponse;
import com.mypill.domain.member.entity.Member;
import com.mypill.domain.nutrient.entity.Nutrient;
import com.mypill.domain.order.entity.Order;
import com.mypill.domain.order.entity.OrderItem;
import com.mypill.domain.order.entity.OrderStatus;
import com.mypill.domain.order.service.OrderService;
import com.mypill.domain.product.dto.response.ProductsResponse;
import com.mypill.domain.product.entity.Product;
import com.mypill.domain.product.service.ProductService;
import com.mypill.domain.productlike.service.ProductLikeService;
import com.mypill.global.rq.Rq;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@RequestMapping("/buyer")
@Tag(name = "BuyerController", description = "구매자 회원")
public class BuyerController {
    private final ProductService productService;
    private final ProductLikeService productLikeService;
    private final OrderService orderService;
    private final AddressService addressService;
    private final Rq rq;

    @PreAuthorize("hasAuthority('BUYER')")
    @GetMapping("/myLikes")
    @Operation(summary = "내 관심상품 페이지")
    public String myLikes(Model model) {
        List<Long> productIds = productLikeService.findProductIdsByMemberId(rq.getMember().getId());
        List<Product> products = productService.findByIdIn(productIds);
        model.addAttribute("response", ProductsResponse.of(products));
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
        List<Order> orders = orderService.findByBuyerId(rq.getMember().getId());
        List<OrderItem> orderItems = orderService.findOrderItemByBuyerId(rq.getMember().getId());
        Map<OrderStatus, Long> orderStatusCount = orderService.getOrderStatusCount(orderItems);
        model.addAttribute("response", MyOrderResponse.of(orders, orderStatusCount));
        return "usr/buyer/myOrder";
    }

    @PreAuthorize("hasAuthority('BUYER')")
    @GetMapping("/myAddress")
    @Operation(summary = "내 배송지 페이지")
    public String myAddress(Model model) {
        List<Address> addresses = addressService.findByMemberId(rq.getMember().getId());
        List<AddressResponse> addressResponses = addresses.stream()
                .map(AddressResponse::of).toList();
        model.addAttribute("addresses", addressResponses);
        return "usr/buyer/myAddress";
    }

    @PreAuthorize("hasAuthority('BUYER')")
    @GetMapping("/mySurvey")
    @Operation(summary = "내 설문 페이지")
    public String mySurvey(Model model) {
        Member member = rq.getMember();
        List<Nutrient> nutrients = member.getSurveyNutrients();
        model.addAttribute("nutrients", nutrients);
        return "usr/buyer/mySurvey";
    }
}