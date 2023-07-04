package com.mypill.domain.buyer.controller;

import com.mypill.domain.address.dto.response.AddressResponse;
import com.mypill.domain.address.entity.Address;
import com.mypill.domain.address.service.AddressService;
import com.mypill.domain.comment.entity.Comment;
import com.mypill.domain.comment.service.CommentService;
import com.mypill.domain.order.dto.response.OrderListResponse;
import com.mypill.domain.order.entity.Order;
import com.mypill.domain.order.entity.OrderItem;
import com.mypill.domain.order.entity.OrderStatus;
import com.mypill.domain.order.service.OrderService;
import com.mypill.domain.post.entity.Post;
import com.mypill.domain.post.service.PostService;
import com.mypill.global.rq.Rq;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
@RequestMapping("/usr/buyer")
public class BuyerController {
    private final PostService postService;
    private final CommentService commentService;
    private final OrderService orderService;
    private final AddressService addressService;
    private final Rq rq;

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/myPage")
    public String myPage() {
        return "usr/buyer/myPage";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/myInfo")
    public String myInfo() {
        return "usr/buyer/myInfo";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/myLikes")
    public String myLikes() {
        return "usr/buyer/myLikes";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/myPosts")
    public String myPosts(Model model) {
        List<Post> posts = postService.getList(rq.getMember());
        model.addAttribute("posts", posts);
        return "usr/buyer/myPosts";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/myComments")
    public String myComments(Model model) {
        List<Comment> comments = commentService.getMyComments(rq.getMember());
        model.addAttribute("comments", comments);
        return "usr/buyer/myComments";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/mySchedule")
    public String mySchedule() {
        return "usr/buyer/mySchedule";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/myOrder")
    public String myOrder(Model model) {

        List<Order> orders = orderService.findByBuyerId(rq.getMember().getId());
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

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/myAddress")
    public String myAddress(Model model) {

        List<Address> addresses = addressService.findByMemberId(rq.getMember().getId());
        List<AddressResponse> addressResponses = addresses.stream()
                .filter(address -> address.getDeleteDate() == null)
                .map(AddressResponse::of).toList();
        model.addAttribute("addresses",addressResponses);

        return "usr/buyer/myAddress";
    }
}
