package com.mypill.domain.diary.controller;

import com.mypill.domain.category.entity.Category;
import com.mypill.domain.category.service.CategoryService;
import com.mypill.domain.nutrient.Service.NutrientService;
import com.mypill.domain.nutrient.entity.Nutrient;
import com.mypill.domain.product.dto.request.ProductRequest;
import com.mypill.domain.product.dto.response.ProductResponse;
import com.mypill.global.rq.Rq;
import com.mypill.global.rsData.RsData;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/usr/diary")
public class diaryController {

    private final NutrientService nutrientService;
    private final CategoryService categoryService;
    private final Rq rq;


    @PreAuthorize("isAuthenticated()")
    @GetMapping("/list")
    @Operation(summary = "나의 영양제 리스트")
    public String showList() {

        return "usr/diary/list";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/create")
    @Operation(summary = "영양제 등록 폼")
    public String create(Model model) {

        populateModel(model);
        return "usr/diary/create";
    }

//    @PreAuthorize("isAuthenticated()")
//    @PostMapping("/create")
//    @Operation(summary = "영양제 등록")
//    public String create(@) {
//
//
//    }



    private void populateModel(Model model) {
        List<Nutrient> nutrients = nutrientService.findAllByOrderByNameAsc();
        List<Category> categories = categoryService.findAllByOrderByNameAsc();

        model.addAttribute("nutrients", nutrients);
        model.addAttribute("categories", categories);
    }

}
