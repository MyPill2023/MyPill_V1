package com.mypill.domain.home.controller;


import com.mypill.domain.product.dto.response.ProductsResponse;
import com.mypill.domain.product.entity.Product;
import com.mypill.domain.product.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
@Tag(name = "HomeController", description = "홈")
public class HomeController {

    private final ProductService productService;

    @GetMapping("/")
    @Operation(summary = "메인 페이지")
    public String showMain(Model model) {
        List<Product> productList = productService.getTop5ProductsBySales();
        model.addAttribute("response", ProductsResponse.of(productList));
        return "usr/home/main";
    }
}
