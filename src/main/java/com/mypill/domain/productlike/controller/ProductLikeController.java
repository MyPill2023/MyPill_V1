package com.mypill.domain.productlike.controller;

import com.mypill.domain.productlike.entity.ProductLike;
import com.mypill.domain.productlike.service.ProductLikeService;
import com.mypill.global.rq.Rq;
import com.mypill.global.rsdata.RsData;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/product/like")
public class ProductLikeController {

    private final ProductLikeService productLikeService;
    private final Rq rq;

    @ResponseBody
    @PreAuthorize("hasAuthority('BUYER')")
    @PostMapping("/create/{id}")
    @Operation(summary = "상품 좋아요 등록")
    public RsData<ProductLike> likeProduct(@PathVariable Long id) {
        return productLikeService.like(rq.getMember().getId(), id);
    }

    @ResponseBody
    @PreAuthorize("hasAuthority('BUYER')")
    @PostMapping("/delete/{id}")
    @Operation(summary = "상품 좋아요 취소")
    public RsData<ProductLike> unlikeProduct(@PathVariable Long id) {
        return productLikeService.unlike(rq.getMember().getId(), id);
    }

    @PreAuthorize("hasAuthority('BUYER')")
    @GetMapping("/delete/{id}")
    @Operation(summary = "관심 상품 목록에서 좋아요 삭제")
    public String unlike(@PathVariable Long id) {
        RsData<ProductLike> unlikeRsData = productLikeService.unlike(rq.getMember().getId(), id);
        if (unlikeRsData.isFail()) {
            return rq.historyBack(unlikeRsData);
        }
        return rq.redirectWithMsg("/buyer/myLikes", "관심 상품이 삭제되었습니다.");
    }
}
