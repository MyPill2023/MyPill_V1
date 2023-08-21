package com.mypill.domain.productlike.service;

import com.mypill.domain.product.entity.Product;
import com.mypill.domain.product.service.ProductService;
import com.mypill.domain.productlike.entity.ProductLike;
import com.mypill.domain.productlike.repository.ProductLikeRepository;
import com.mypill.global.event.EventAfterLike;
import com.mypill.global.event.EventAfterUnlike;
import com.mypill.global.rsdata.RsData;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductLikeService {

    private final ProductService productService;
    private final ProductLikeRepository productLikeRepository;
    private final ApplicationEventPublisher publisher;

    @Transactional
    public RsData<ProductLike> like(Long memberId, Long productId) {
        Product product = productService.findById(productId).orElse(null);
        if (product == null) {
            return RsData.of("F-1", "존재하지 않는 상품입니다.");
        }

        ProductLike productLike = findByMemberIdAndProductId(memberId, productId);
        if (productLike != null) {
            return RsData.of("F-2", "이미 좋아요 한 상품입니다.");
        }
        ProductLike newProductLike = new ProductLike(memberId, productId);
        productLikeRepository.save(newProductLike);
        publisher.publishEvent(new EventAfterLike(this, product));
        return RsData.of("S-1", "상품 좋아요가 등록되었습니다.");
    }

    @Transactional
    public RsData<ProductLike> unlike(Long memberId, Long productId) {
        Product product = productService.findById(productId).orElse(null);
        if (product == null) {
            return RsData.of("F-1", "존재하지 않는 상품입니다.");
        }

        ProductLike productLike = findByMemberIdAndProductId(memberId, productId);
        if (productLike == null) {
            return RsData.of("F-1", "이미 좋아요 취소한 상품입니다.");
        }
        productLikeRepository.delete(productLike);
        publisher.publishEvent(new EventAfterUnlike(this, product));
        return RsData.of("S-1", "상품 좋아요 취소");
    }

    public ProductLike findByMemberIdAndProductId(Long memberId, Long productId) {
        return productLikeRepository.findByMemberIdAndProductId(memberId, productId);
    }
}
