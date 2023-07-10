package com.mypill.domain.home.controller;

import com.mypill.domain.product.dto.response.ProductResponse;
import com.mypill.domain.product.entity.Product;
import com.mypill.domain.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final ProductService productService;

    @GetMapping("/")
    public String showMain(Model model) {

        List<ProductResponse> productList = productService.findTop5ProductsBySales()
                .stream().map(ProductResponse::of).toList();
        model.addAttribute("products", productList);

        return "usr/home/main";
    }
}
