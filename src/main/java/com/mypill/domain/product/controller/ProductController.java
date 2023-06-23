package com.mypill.domain.product.controller;

import com.mypill.domain.category.entity.Category;
import com.mypill.domain.category.service.CategoryService;
import com.mypill.domain.nutrient.Service.NutrientService;
import com.mypill.domain.nutrient.entity.Nutrient;
import com.mypill.domain.product.Service.ProductService;
import com.mypill.domain.product.dto.request.ProductRequest;
import com.mypill.domain.product.dto.response.ProductResponse;
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
    private final ProductService productService;
    private final NutrientService nutrientService;
    private final CategoryService categoryService;
    private final Rq rq;

    @GetMapping("/create")
    public String showCreate(Model model){

        List<Nutrient> nutrients = nutrientService.findAllByOrderByNameAsc();
        model.addAttribute("nutrients", nutrients);

        List<Category> categories = categoryService.findAllByOrderByNameAsc();
        model.addAttribute("categories", categories);

        return "usr/product/create";
    }

    @PostMapping("/create")
    public String create(@Valid ProductRequest productRequest){

        RsData<ProductResponse> createRsData = productService.create(productRequest);

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

        model.addAttribute("products", productService.getAllProduct(products));

        return "usr/product/list";
    }

    @PostMapping("/update/{productId}")
    public String update(@PathVariable Long productId, @Valid ProductRequest productRequest){

        RsData<ProductResponse> updateRsData = productService.update(productId, productRequest);

        return rq.redirectWithMsg("/product/list", updateRsData.getMsg());
    }

}
