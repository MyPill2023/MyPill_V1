package com.mypill.domain.seller.controller;

import com.mypill.domain.member.entity.Member;
import com.mypill.domain.order.entity.Order;
import com.mypill.domain.order.entity.OrderItem;
import com.mypill.domain.order.entity.OrderStatus;
import com.mypill.domain.order.service.OrderService;
import com.mypill.domain.product.entity.Product;
import com.mypill.domain.product.service.ProductService;
import com.mypill.domain.seller.dto.OrderManagementResponse;
import com.mypill.domain.seller.service.SellerService;
import com.mypill.global.rq.Rq;
import com.mypill.global.rsdata.RsData;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
@RequestMapping("/seller")
@Tag(name = "SellerController", description = "판매자 회원")
public class SellerController {
    private final SellerService sellerService;
    private final OrderService orderService;
    private final ProductService productService;
    private final Rq rq;

    @PreAuthorize("hasAuthority('SELLER')")
    @GetMapping("/order")
    @Operation(summary = "주문 관리 페이지")
    public String showMyOrder(Model model) {
        List<Order> orders = orderService.findBySellerId(rq.getMember().getId())
                .stream()
                .sorted(Comparator.comparing((Order order) -> order.getPayment().getPayDate()).reversed())
                .toList();

        List<OrderItem> orderItems = orderService.findOrderItemBySellerId(rq.getMember().getId());
        Map<OrderStatus, Long> orderStatusCount = orderItems.stream()
                .collect(Collectors.groupingBy(OrderItem::getStatus, Collectors.counting()));

        model.addAttribute("response", OrderManagementResponse.of(orders, orderItems, orderStatusCount));
        return "usr/seller/orderList";
    }

    @PreAuthorize("hasAuthority('WAITER')")
    @GetMapping("/certificate")
    @Operation(summary = "판매자 인증 페이지")
    public String showCertificate() {
        return "usr/seller/certificate";
    }

    @PreAuthorize("hasAuthority('WAITER')")
    @PostMapping("/brnoCertificate")
    @Operation(summary = "통신판매업 인증")
    public String brnoCertificate(@RequestParam("businessNumber") String businessNumber) {
        RsData<Member> checkRsData = sellerService.businessNumberCheck(businessNumber, rq.getMember());
        if (checkRsData.isFail()) {
            return rq.historyBack(checkRsData);
        }
        return rq.redirectWithMsg("/seller/certificate", checkRsData);
    }

    @PreAuthorize("hasAuthority('WAITER')")
    @PostMapping("/nBrnoCertificate")
    @Operation(summary = "건강기능식품 판매업 인증")
    public String nBrnoCertificate(@RequestParam("nutrientBusinessNumber") String nutrientBusinessNumber) {
        RsData<Member> checkRsData = sellerService.nutrientBusinessNumberCheck(nutrientBusinessNumber, rq.getMember());
        if (checkRsData.isFail()) {
            return rq.historyBack(checkRsData);
        }
        return rq.redirectWithMsg("/seller/certificate", checkRsData);
    }

    @PreAuthorize("hasAuthority('SELLER')")
    @GetMapping("/myProduct")
    @Operation(summary = "상품 관리 페이지")
    public String showMyProduct(@RequestParam(defaultValue = "0") int pageNumber,
                                @RequestParam(defaultValue = "10") int pageSize,
                                Model model) {
        Page<Product> products = productService.getAllProductBySellerId(rq.getMember().getId(), PageRequest.of(pageNumber, pageSize));
        model.addAttribute("product", products);
        return "usr/seller/myProduct";
    }

    @PreAuthorize("hasAuthority('SELLER')")
    @GetMapping("/chart")
    @Operation(summary = "통계 페이지")
    public String showChart(Model model) {
        Map<YearMonth, Long> yearMonthLongMap = orderService.countOrderPrice(rq.getMember().getId());
        List<String> labels = yearMonthLongMap.keySet()
                .stream()
                .map(yearMonth -> yearMonth.getMonth().toString())
                .collect(Collectors.toList());
        List<Long> salesData = new ArrayList<>(yearMonthLongMap.values());
        model.addAttribute("labels", labels);
        model.addAttribute("salesData", salesData);
        return "usr/seller/chart";
    }
}
