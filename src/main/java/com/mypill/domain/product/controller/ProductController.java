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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
@RequestMapping("/product")
@Tag(name = "ProductController", description = "상품")
public class ProductController {
    private final ProductService productService;
    private final NutrientService nutrientService;
    private final CategoryService categoryService;
    private final Rq rq;

    @GetMapping("/create")
    @Operation(summary = "상품 등록 폼")
    public String showCreate(Model model){

        List<Nutrient> nutrients = nutrientService.findAllByOrderByNameAsc();
        model.addAttribute("nutrients", nutrients);

        List<Category> categories = categoryService.findAllByOrderByNameAsc();
        model.addAttribute("categories", categories);

        return "usr/product/create";
    }

    @PostMapping("/create")
    @Operation(summary = "상품 등록")
    public String create(@Valid ProductRequest productRequest){

        RsData<ProductResponse> createRsData = productService.create(productRequest);

        return rq.redirectWithMsg("/product/detail/%s".formatted(createRsData.getData().getId()), createRsData);
    }

    @GetMapping("/detail/{productId}")
    @Operation(summary = "상품 상세")
    public String showProduct(@PathVariable Long productId, Model model){

        model.addAttribute("productResponse", productService.get(productId).getData());

        return "usr/product/detail";
    }

    @GetMapping("/list")
    @Operation(summary = "상품 전체 목록")
    public String list(Model model){
        List<Product> products = productService.findNotDeleted();

        model.addAttribute("products", productService.getAllProduct(products));

        return "usr/product/list";
    }

    @GetMapping("/update/{productId}")
    @Operation(summary = "상품 수정 폼")
    public String updateBefore(@PathVariable Long productId, Model model){

        ProductResponse response = productService.get(productId).getData();
        model.addAttribute("product", response);

        List<Nutrient> nutrients = nutrientService.findAllByOrderByNameAsc();
        model.addAttribute("nutrients", nutrients);

        List<Category> categories = categoryService.findAllByOrderByNameAsc();
        model.addAttribute("categories", categories);

        return "usr/product/update";
    }

    @PostMapping("/update/{productId}")
    @Operation(summary = "상품 수정")
    public String update(@PathVariable Long productId, @Valid ProductRequest productRequest){

        RsData<ProductResponse> updateRsData = productService.update(productId, productRequest);

        return rq.redirectWithMsg("/product/list", updateRsData);
    }

    @PostMapping("/delete/{productId}")
    @Operation(summary = "상품 수정")
    public String update(@PathVariable Long productId){

        RsData<ProductResponse> deleteRsData = productService.delete(productId);

        return rq.redirectWithMsg("/product/list", deleteRsData);
    }

}
