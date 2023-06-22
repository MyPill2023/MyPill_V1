package com.mypill.domain.product.controller;

import com.mypill.domain.product.Service.ProductService;
import com.mypill.domain.product.dto.request.ProductRequestDto;
import com.mypill.domain.product.dto.response.ProductResponseDto;
import com.mypill.domain.product.entity.Product;
import com.mypill.global.rq.Rq;
import com.mypill.global.rsData.RsData;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/product")
public class ProductController {
    private ProductService productService;
    private Rq rq;

    @GetMapping("/create")
    public String showCreate(Model model){
        return "usr/product/create";
    }

    @PostMapping("/create")
    public String create(@Valid ProductRequestDto productRequestDto){

        RsData<ProductResponseDto> createRsData = productService.create(productRequestDto);

        return rq.redirectWithMsg("/product/detail/%s".formatted(createRsData.getData().getId()), createRsData);
    }

    @GetMapping("/detail/{productId}")
    public String showProduct(@PathVariable Long productId, Model model){

        model.addAttribute("productResponse", productService.get(productId));

        return "usr/product/detail";
    }

    @GetMapping("/list")
    public String list(Model model){
        List<Product> products = productService.findAll();

        model.addAttribute("products", products);

        return "usr/product/list";
    }

    @PostMapping("/update/{productId}")
    public String update(@PathVariable Long productId, @Valid ProductRequestDto productRequestDto){

        RsData<ProductResponseDto> updateRsData = productService.update(productId, productRequestDto);

        return rq.redirectWithMsg("/product/list", updateRsData.getMsg());
    }

}
