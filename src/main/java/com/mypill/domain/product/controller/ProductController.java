package com.mypill.domain.product.controller;

import com.mypill.domain.category.entity.Category;
import com.mypill.domain.category.service.CategoryService;
import com.mypill.domain.nutrient.entity.Nutrient;
import com.mypill.domain.nutrient.service.NutrientService;
import com.mypill.domain.product.dto.request.ProductRequest;
import com.mypill.domain.product.dto.response.ProductPageResponse;
import com.mypill.domain.product.dto.response.ProductResponse;
import com.mypill.domain.product.entity.Product;
import com.mypill.domain.product.service.ProductService;
import com.mypill.global.rq.Rq;
import com.mypill.global.rsdata.RsData;
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
    @Operation(summary = "상품 등록 페이지")
    public String showCreateForm(Model model) {
        populateModel(model);
        return "usr/product/create";
    }

    @PreAuthorize("hasAuthority('SELLER')")
    @PostMapping("/create")
    @Operation(summary = "상품 등록")
    public String create(@Valid ProductRequest productRequest) {
        RsData<Product> createRsData = productService.create(productRequest, rq.getMember());
        return rq.redirectWithMsg("/product/detail/%s".formatted(createRsData.getData().getId()), createRsData);
    }

    @GetMapping("/detail/{productId}")
    @Operation(summary = "상품 상세 페이지")
    public String showProduct(@PathVariable Long productId, Model model) {
        RsData<Product> productRsData = productService.get(productId);
        if (productRsData.isFail()) {
            return rq.historyBack(productRsData);
        }
        if (rq.isLogin() && productRsData.getData().getLikedMembers().contains(rq.getMember())) {
            model.addAttribute("response", ProductResponse.of(productRsData.getData(), true));
        } else {
            model.addAttribute("response", ProductResponse.of(productRsData.getData(), false));
        }
        return "usr/product/detail";
    }

    @GetMapping("/list/all")
    @Operation(summary = "상품 전체 목록 페이지")
    public String showList(Model model, HttpServletRequest request,
                           @RequestParam(defaultValue = "0") int pageNumber,
                           @RequestParam(defaultValue = "10") int pageSize) {
        Page<Product> productPage = productService.getAllProductList(PageRequest.of(pageNumber, pageSize));
        populateModel(model, "전체보기", productPage, request);
        return "usr/product/list";
    }

    @GetMapping("/list/nutrient/{nutrientId}")
    @Operation(summary = "영양 성분별 상품 목록 페이지")
    public String listByNutrient(@PathVariable Long nutrientId,
                                 @RequestParam(defaultValue = "0") int pageNumber,
                                 @RequestParam(defaultValue = "10") int pageSize,
                                 Model model, HttpServletRequest request) {
        Page<Product> productPage = productService.getAllProductListByNutrientId(nutrientId, PageRequest.of(pageNumber, pageSize));
        Nutrient nutrient = nutrientService.findById(nutrientId).orElse(null);
        if (nutrient == null) {
            return rq.historyBack("잘못된 접근입니다.");
        }
        populateModel(model, nutrient.getName(), productPage, request);
        return "usr/product/list";
    }

    @GetMapping("/list/category/{categoryId}")
    @Operation(summary = "주요 기능별 상품 목록 페이지")
    public String listByCategory(@PathVariable Long categoryId,
                                 @RequestParam(defaultValue = "0") int pageNumber,
                                 @RequestParam(defaultValue = "10") int pageSize,
                                 Model model, HttpServletRequest request) {
        Page<Product> productPage = productService.getAllProductListByCategoryId(categoryId, PageRequest.of(pageNumber, pageSize));
        Category category = categoryService.findById(categoryId).orElse(null);
        if (category == null) {
            return rq.historyBack("잘못된 접근입니다.");
        }
        populateModel(model, category.getName(), productPage, request);
        return "usr/product/list";
    }

    @PreAuthorize("hasAuthority('SELLER')")
    @GetMapping("/update/{productId}")
    @Operation(summary = "상품 수정 페이지")
    public String showUpdateForm(@PathVariable Long productId, Model model) {
        RsData<Product> productRsData = productService.get(productId);
        if (productRsData.isFail()) {
            return rq.historyBack(productRsData);
        }
        model.addAttribute("response", ProductResponse.of(productRsData.getData()));
        populateModel(model);
        return "usr/product/update";
    }

    @PreAuthorize("hasAuthority('SELLER')")
    @PostMapping("/update/{productId}")
    @Operation(summary = "상품 수정")
    public String update(@PathVariable Long productId, @Valid ProductRequest productRequest) {
        RsData<Product> updateRsData = productService.update(rq.getMember(), productId, productRequest);
        if (updateRsData.isFail()) {
            return rq.historyBack(updateRsData);
        }
        return rq.redirectWithMsg("/product/detail/%s".formatted(productId), updateRsData);
    }

    @PostMapping("/delete/{productId}")
    @PreAuthorize("hasAuthority('SELLER')")
    @Operation(summary = "상품 삭제")
    public String delete(@PathVariable Long productId) {
        RsData<Product> deleteRsData = productService.softDelete(rq.getMember(), productId);
        if (deleteRsData.isFail()) {
            return rq.historyBack(deleteRsData);
        }
        return rq.redirectWithMsg("/seller/myProduct", deleteRsData);
    }

    @ResponseBody
    @PreAuthorize("hasAuthority('BUYER')")
    @PostMapping("/like/{id}")
    @Operation(summary = "상품 좋아요 등록")
    public RsData<Product> likeProduct(@PathVariable("id") Long id) {
        return productService.like(rq.getMember(), id);
    }

    @ResponseBody
    @PreAuthorize("hasAuthority('BUYER')")
    @PostMapping("/unlike/{id}")
    @Operation(summary = "상품 좋아요 취소")
    public RsData unlikeProduct(@PathVariable("id") Long id) {
        return productService.unlike(rq.getMember(), id);
    }

    @PreAuthorize("hasAuthority('BUYER')")
    @GetMapping("/unlike/{id}")
    @Operation(summary = "관심 상품 목록에서 좋아요 삭제")
    public String unlike(@PathVariable("id") Long id) {
        RsData<Product> unlikeRsData = productService.unlike(rq.getMember(), id);
        if (unlikeRsData.isFail()) {
            return rq.historyBack(unlikeRsData);
        }
        return rq.redirectWithMsg("/buyer/myLikes", "관심 상품이 삭제되었습니다.");
    }

    private void populateModel(Model model) {
        List<Nutrient> nutrients = nutrientService.findAllByOrderByNameAsc();
        List<Category> categories = categoryService.findAllByOrderByNameAsc();
        model.addAttribute("nutrients", nutrients);
        model.addAttribute("categories", categories);
    }

    private void populateModel(Model model, String title, Page<Product> productPage, HttpServletRequest request) {
        List<Nutrient> nutrients = nutrientService.findAllByOrderByNameAsc();
        List<Category> categories = categoryService.findAllByOrderByNameAsc();
        model.addAttribute("response", ProductPageResponse.of(title, productPage, nutrients, categories, getPagingUrl(request)));
    }

    private String getPagingUrl(HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        if (requestURI.contains("/list/all")) {
            return "/product/list/all?";
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
