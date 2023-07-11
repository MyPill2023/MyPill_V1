package com.mypill.domain.cart.controller;

import com.mypill.domain.cart.dto.request.CartProductRequest;
import com.mypill.domain.cart.dto.response.CartResponse;
import com.mypill.domain.cart.entity.Cart;
import com.mypill.domain.cart.entity.CartProduct;
import com.mypill.domain.cart.service.CartService;
import com.mypill.global.rq.Rq;
import com.mypill.global.rsdata.RsData;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
@RequestMapping("/cart")
@Tag(name = "CartController", description = "장바구니")
public class CartController {

    private final CartService cartService;
    private final Rq rq;

    @PreAuthorize("hasAuthority('BUYER')")
    @GetMapping("")
    @Operation(summary = "장바구니 페이지")
    public String showCart(Model model) {
        Cart cart = cartService.getCart(rq.getMember());
        model.addAttribute("cartResponse", CartResponse.of(cart));
        return "usr/cart/list";
    }

    @PreAuthorize("hasAuthority('BUYER')")
    @PostMapping("/add")
    @Operation(summary = "장바구니에 상품 추가")
    public String addCartProduct(@Valid CartProductRequest request) {
        RsData<CartProduct> addRsData = cartService.addProduct(rq.getMember(), request);
        if (addRsData.isFail()) {
            return rq.historyBack(addRsData);
        }
        return rq.redirectWithMsg("/product/detail/%s".formatted(request.getProductId()), addRsData);
    }

    @PreAuthorize("hasAuthority('BUYER')")
    @PostMapping("/update")
    @Operation(summary = "장바구니에서 상품 수량 업데이트")
    public String updateQuantity(@RequestParam Long cartProductId, @RequestParam Long newQuantity) {
        RsData<CartProduct> updateRsData = cartService.updateQuantity(rq.getMember(), cartProductId, newQuantity);
        if (updateRsData.getResultCode().equals("F-2")) {
            return rq.redirectWithMsg("/cart", updateRsData);
        }
        if (updateRsData.isFail()) {
            return rq.historyBack(updateRsData);
        }
        return rq.redirectWithMsg("/cart", updateRsData);
    }

    @PreAuthorize("hasAuthority('BUYER')")
    @PostMapping("/delete")
    @Operation(summary = "장바구니에서 상품 삭제")
    public String softDeleteCartProduct(@RequestParam Long cartProductId) {
        RsData<CartProduct> deleteRsData = cartService.softDeleteCartProduct(rq.getMember(), cartProductId);
        if (deleteRsData.isFail()) {
            return rq.historyBack(deleteRsData);
        }
        return rq.redirectWithMsg("/cart", deleteRsData);
    }
}