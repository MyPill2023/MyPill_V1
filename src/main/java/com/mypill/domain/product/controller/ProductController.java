package com.mypill.domain.product.controller;

import com.mypill.domain.category.entity.Category;
import com.mypill.domain.category.service.CategoryService;
import com.mypill.domain.nutrient.entity.Nutrient;
import com.mypill.domain.nutrient.service.NutrientService;
import com.mypill.domain.product.dto.request.ProductRequest;
import com.mypill.domain.product.dto.response.ProductResponse;
import com.mypill.domain.product.entity.Product;
import com.mypill.domain.product.service.ProductService;
import com.mypill.global.rq.Rq;
import com.mypill.global.rsData.RsData;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/product")
@Tag(name = "ProductController", description = "상품")
public class ProductController {
    private final ProductService productService;
    private final NutrientService nutrientService;
    private final CategoryService categoryService;
    private final Rq rq;

    @PreAuthorize("hasAuthority('SELLER')")
    @GetMapping("/create")
    @Operation(summary = "상품 등록 폼")
    public String showCreate(Model model) {

        populateModel(model);

        return "usr/product/create";
    }

    @PreAuthorize("hasAuthority('SELLER')")
    @PostMapping("/create")
    @Operation(summary = "상품 등록")
    public String create(@Valid ProductRequest productRequest) {

        RsData<Product> createRsData = productService.create(productRequest);

        return rq.redirectWithMsg("/product/detail/%s".formatted(createRsData.getData().getId()), createRsData);
    }

    @GetMapping("/detail/{productId}")
    @Operation(summary = "상품 상세")
    public String showProduct(@PathVariable Long productId, Model model) {

        Product product = productService.get(productId).getData();
        if (rq.isLogin() && product.getLikedMembers().contains(rq.getMember())) {
            model.addAttribute("product", ProductResponse.of(product, true));
        }else {
            model.addAttribute("product", ProductResponse.of(product, false));
        }

        return "usr/product/detail";
    }

    @GetMapping("/list/all")
    @Operation(summary = "상품 전체 목록")
    public String list(Model model, HttpServletRequest request,
                       @RequestParam(defaultValue = "0") int pageNumber,
                       @RequestParam(defaultValue = "10") int pageSize) {
        Page<Product> productPageResult = productService.getAllProductList(PageRequest.of(pageNumber, pageSize));
        model.addAttribute("title", "전체보기");
        populateModel(model, productPageResult, request, pageNumber, pageSize);
        return "usr/product/list";
    }

    @GetMapping("/list/nutrient/{nutrientId}")
    @Operation(summary = "영양 성분별 상품 목록")
    public String listByNutrition(@PathVariable Long nutrientId,
                                  @RequestParam(defaultValue = "0") int pageNumber,
                                  @RequestParam(defaultValue = "10") int pageSize,
                                  Model model, HttpServletRequest request) {
        Page<Product> productPageResult = productService.getAllProductListByNutrientId(nutrientId, PageRequest.of(pageNumber, pageSize));
        nutrientService.findById(nutrientId).ifPresent(nutrient -> model.addAttribute("title", nutrient.getName()));
        populateModel(model, productPageResult, request, pageNumber, pageSize);
        return "usr/product/list";
    }

    @GetMapping("/list/category/{categorytId}")
    @Operation(summary = "주요 기능별 상품 목록")
    public String listByCategory(@PathVariable Long categorytId,
                                 @RequestParam(defaultValue = "0") int pageNumber,
                                 @RequestParam(defaultValue = "10") int pageSize,
                                 Model model, HttpServletRequest request) {
        Page<Product> productPageResult = productService.getAllProductListByCategoryId(categorytId, PageRequest.of(pageNumber, pageSize));
        categoryService.findById(categorytId).ifPresent(category -> model.addAttribute("title", category.getName()));
        populateModel(model, productPageResult, request, pageNumber, pageSize);
        return "usr/product/list";
    }

    @PreAuthorize("hasAuthority('SELLER')")
    @GetMapping("/update/{productId}")
    @Operation(summary = "상품 수정 폼")
    public String updateBefore(@PathVariable Long productId, Model model) {

        ProductResponse response = ProductResponse.of(productService.get(productId).getData());
        model.addAttribute("product", response);
        populateModel(model);

        return "usr/product/update";
    }

    @PreAuthorize("hasAuthority('SELLER')")
    @PostMapping("/update/{productId}")
    @Operation(summary = "상품 수정")
    public String update(@PathVariable Long productId, @Valid ProductRequest productRequest) {

        RsData<Product> updateRsData = productService.update(rq.getMember(), productId, productRequest);

        return rq.redirectWithMsg("/product/detail/%s".formatted(productId), updateRsData);
    }


    @PostMapping("/delete/{productId}")
    @PreAuthorize("hasAuthority('SELLER')")
    @Operation(summary = "상품 삭제")
    public String delete(@PathVariable Long productId) {

        RsData<Product> deleteRsData = productService.delete(rq.getMember(), productId);

        return rq.redirectWithMsg("/product/list/all", deleteRsData);
    }

    @ResponseBody
    @PreAuthorize("hasAuthority('BUYER')")
    @PostMapping("/like/{id}")
    public Integer likeProduct(@PathVariable("id") Long id) {
        return productService.like(rq.getMember(), id);
    }

    @ResponseBody
    @PreAuthorize("hasAuthority('BUYER')")
    @PostMapping("/unlike/{id}")
    public Integer unlikeProduct(@PathVariable("id") Long id) {
        return productService.unlike(rq.getMember(), id);
    }

    @PreAuthorize("hasAuthority('BUYER')")
    @GetMapping("/unlike/{id}")
    public String unlike(@PathVariable("id") Long id) {
        productService.unlike(rq.getMember(), id);
        return rq.redirectWithMsg("/usr/buyer/myLikes","관심 상품이 삭제되었습니다.");
    }

    private List<ProductResponse> convertToResponse(List<Product> products) {
        return products.stream().map(ProductResponse::of).toList();
    }

    private void populateModel(Model model) {
        List<Nutrient> nutrients = nutrientService.findAllByOrderByNameAsc();
        List<Category> categories = categoryService.findAllByOrderByNameAsc();
        model.addAttribute("nutrients", nutrients);
        model.addAttribute("categories", categories);
    }

    private void populateModel(Model model, Page<Product> productPageResult, HttpServletRequest request, int pageNumber, int pageSize) {
        List<Nutrient> nutrients = nutrientService.findAllByOrderByNameAsc();
        List<Category> categories = categoryService.findAllByOrderByNameAsc();
        model.addAttribute("nutrients", nutrients);
        model.addAttribute("categories", categories);

        model.addAttribute("page", productPageResult);
        model.addAttribute("products", convertToResponse(productPageResult.getContent()));
        model.addAttribute("pagingUrl", getPagingUrl(request, pageNumber, pageSize));

    }

    private String getPagingUrl(HttpServletRequest request, int pageNumber, int pageSize) {
        String requestURI = request.getRequestURI();

        if (requestURI.contains("/list/all")) {
            return String.format("/product/list/all?");
        } else if (requestURI.contains("/list/nutrient/")) {
            String nutrientId = requestURI.substring(requestURI.lastIndexOf('/') + 1);
            return String.format("/product/list/nutrient/%s", nutrientId);
        } else if (requestURI.contains("/list/category/")) {
            String categoryId = requestURI.substring(requestURI.lastIndexOf('/') + 1);
            return String.format("/product/list/category/%s", categoryId);
        } else {
            return "";
        }
    }


}
