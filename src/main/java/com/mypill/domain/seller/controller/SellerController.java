package com.mypill.domain.seller.controller;

import com.mypill.domain.order.dto.response.OrderListResponse;
import com.mypill.domain.order.entity.Order;
import com.mypill.domain.order.entity.Payment;
import com.mypill.domain.order.service.OrderService;
import com.mypill.domain.seller.service.SellerService;
import com.mypill.global.rq.Rq;
import com.mypill.global.rsData.RsData;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/usr/seller")
public class SellerController {
    private final SellerService sellerService;

    private final OrderService orderService;
    private final Rq rq;

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/myPage")
    public String main() {
        return "usr/buyer/myPage";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/myInfo")
    public String myInfo() {
        return "usr/seller/myInfo";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/order")
    public String orderManagement(Model model) {
        List<OrderListResponse> orderResponses = orderService.findBySellerId(rq.getMember().getId())
                .stream()
                .sorted(Comparator.comparing((Order order) -> order.getPayment().getPayDate()).reversed())
                .map(OrderListResponse::of).toList();

        model.addAttribute("orders", orderResponses);
        return "usr/seller/orderList";
    }

    @GetMapping("/certificate")
    public String certificate() {
        return "usr/seller/certificate";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/brnoCertificate")
    public String brnoCertificate(@RequestParam("brno") String brno) {
        RsData rsData = sellerService.certificateBRNO(brno, rq.getMember());
        if (rsData.isFail()) {
            rq.historyBack(rsData.getMsg());
        }
        return rq.redirectWithMsg("/usr/seller/certificate", rsData);
    }
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/nBrnoCertificate")
    public String nBrnoCertificate(@RequestParam("nBrno") String nBrno) {
        RsData rsData = sellerService.certificateNBRNO(nBrno, rq.getMember());
        if (rsData.isFail()) {
            rq.historyBack(rsData.getMsg());
        }
        return rq.redirectWithMsg("/usr/seller/certificate", rsData);
    }
}
