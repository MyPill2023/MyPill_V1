package com.mypill.domain.cart.controller;

import com.mypill.domain.cart.dto.request.CartProductRequest;
import com.mypill.domain.cart.dto.response.CartResponse;
import com.mypill.domain.cart.entity.Cart;
import com.mypill.domain.cart.entity.CartProduct;
import com.mypill.domain.cart.service.CartService;
import com.mypill.global.rq.Rq;
import com.mypill.global.rsData.RsData;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/cart")
public class CartController {

    private final CartService cartService;
    private final Rq rq;

    @PreAuthorize("hasAuthority('MEMBER')")
    @GetMapping("")
    public String showCart(Model model){

        CartResponse cartResponse =  cartService.cartView();
        model.addAttribute("cartResponse",cartResponse);

        return "usr/cart/list";
    }

    @PreAuthorize("hasAuthority('MEMBER')")
    @PostMapping("/add")
    public String addCartProduct(@Valid CartProductRequest request){
        RsData<CartProduct> addRsData = cartService.addProduct(request);

        if(addRsData.isFail()){
            return rq.historyBack(addRsData);
        }

        return rq.redirectWithMsg("/product/detail/%s".formatted(request.getProductId()), addRsData);
    }

    @PreAuthorize("hasAuthority('MEMBER')")
    @PostMapping("/update")
    public String updateQuantity(@RequestParam Long cartProductId, @RequestParam Long newQuantity){
        RsData<CartProduct> updateRsData = cartService.updateQuantity(cartProductId, newQuantity);

        if(updateRsData.getResultCode().equals("F-2")){
            return rq.redirectWithMsg("/cart", updateRsData);
        }

        if(updateRsData.isFail()){
            return rq.historyBack(updateRsData);
        }

        return rq.redirectWithMsg("/cart", updateRsData);
    }

    @PreAuthorize("hasAuthority('MEMBER')")
    @PostMapping("/delete")
    public String softDeleteCartProduct(@RequestParam Long cartProductId){
        RsData<CartProduct> deleteRsData = cartService.softDeleteCartProduct(cartProductId);

        if(deleteRsData.isFail()){
            return rq.historyBack(deleteRsData);
        }

        return rq.redirectWithMsg("/cart", deleteRsData);
    }

}
