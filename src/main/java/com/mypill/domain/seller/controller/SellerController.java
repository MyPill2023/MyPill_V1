package com.mypill.domain.seller.controller;

import com.mypill.domain.comment.entity.Comment;
import com.mypill.domain.comment.service.CommentService;
import com.mypill.domain.member.service.MemberService;
import com.mypill.domain.order.dto.response.OrderListResponse;
import com.mypill.domain.order.dto.response.OrderResponse;
import com.mypill.domain.order.entity.Order;
import com.mypill.domain.order.service.OrderService;
import com.mypill.domain.post.entity.Post;
import com.mypill.domain.post.service.PostService;
import com.mypill.global.rq.Rq;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/usr/seller")
public class SellerController {

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
                .stream().map(OrderListResponse::of).toList();
        model.addAttribute("orders", orderResponses);
        return "usr/seller/orderManagement";
    }

}
