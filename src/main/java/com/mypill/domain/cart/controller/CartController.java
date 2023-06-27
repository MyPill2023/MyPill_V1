package com.mypill.domain.cart.controller;

import com.mypill.domain.cart.dto.request.CartProductRequest;
import com.mypill.domain.cart.entity.Cart;
import com.mypill.domain.cart.entity.CartProduct;
import com.mypill.domain.cart.service.CartService;
import com.mypill.global.rq.Rq;
import com.mypill.global.rsData.RsData;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/cart")
public class CartController {

    private final CartService cartService;
    private final Rq rq;

    @GetMapping("")
    public String showCart(Model model){

        List<CartProduct> cartProducts = cartService.cartView();
        model.addAttribute("cartProducts", cartProducts);

        return "usr/cart/list";
    }

    @PostMapping("/add")
    public String addProductToCart(@Valid CartProductRequest request, Model model){
        RsData<CartProduct> addRsData = cartService.addProduct(request);

        if(addRsData.isFail()){
            return rq.historyBack(addRsData);
        }

        return rq.redirectWithMsg("/product/detail/%s".formatted(request.getProductId()), addRsData);
    }
}
