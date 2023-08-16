package com.mypill.domain.product.service;

import com.mypill.domain.category.entity.Category;
import com.mypill.domain.category.service.CategoryService;
import com.mypill.domain.image.service.ImageService;
import com.mypill.domain.member.entity.Member;
import com.mypill.domain.nutrient.entity.Nutrient;
import com.mypill.domain.nutrient.service.NutrientService;
import com.mypill.domain.product.dto.request.ProductRequest;
import com.mypill.domain.product.entity.Product;
import com.mypill.domain.product.repository.ProductRepository;
import com.mypill.global.event.EventAfterLike;
import com.mypill.global.event.EventAfterUnlike;
import com.mypill.global.rsdata.RsData;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final NutrientService nutrientService;
    private final CategoryService categoryService;
    private final ApplicationEventPublisher publisher;
    private final ImageService imageService;

    @Transactional
    public RsData<Product> create(ProductRequest request, Member seller) {
        List<Nutrient> nutrients = nutrientService.findByIdIn(request.getNutrientIds());
        List<Category> categories = categoryService.findByIdIn(request.getCategoryIds());
        Product product = Product.of(request, nutrients, categories, seller);
        productRepository.save(product);
        imageService.save(request.getImageFile(), product);
        return RsData.of("S-1", "상품 등록이 완료되었습니다.", product);
    }

    @Transactional(readOnly = true)
    public RsData<Product> get(Long productId) {
        Product product = findById(productId).orElse(null);
        if (product == null) {
            return RsData.of("F-1", "존재하지 않는 상품입니다.");
        }
        return RsData.of("S-1", "존재하는 상품입니다.", product);
    }

    @Transactional
    public RsData<Product> update(Member actor, Long productId, ProductRequest request) {
        Product product = findById(productId).orElse(null);
        if (product == null) {
            return RsData.of("F-1", "존재하지 않는 상품입니다.");
        }
        if (!Objects.equals(actor.getId(),product.getSeller().getId())) {
            return RsData.of("F-2", "수정 권한이 없습니다.");
        }
        List<Nutrient> nutrients = nutrientService.findByIdIn(request.getNutrientIds());
        List<Category> categories = categoryService.findByIdIn(request.getCategoryIds());
        imageService.update(request.getImageFile(), product);
        product.update(request, nutrients, categories);
        return RsData.of("S-1", "상품 수정이 완료되었습니다.", product);
    }

    @Transactional
    public RsData<Product> softDelete(Member actor, Long productId) {
        Product product = findById(productId).orElse(null);
        if (product == null) {
            return RsData.of("F-1", "존재하지 않는 상품입니다.");
        }
        if (!Objects.equals(actor.getId(),product.getSeller().getId())) {
            return RsData.of("F-2", "삭제 권한이 없습니다.");
        }
        product.softDelete();
        return RsData.of("S-1", "상품 삭제가 완료되었습니다.", product);
    }

    @Transactional
    public void updateStockAndSalesByOrder(Long productId, Long quantity) {
        Product lockedProduct = findByIdWithPessimisticLock(productId);
        lockedProduct.updateStockAndSalesByOrder(quantity);
        productRepository.saveAndFlush(lockedProduct);
    }

    public Optional<Product> findById(Long productId) {
        return productRepository.findById(productId);
    }

    public Product findByIdWithPessimisticLock(Long productId) {
        return productRepository.findByIdWithPessimisticLock(productId);
    }

    public List<Product> getTop5ProductsBySales() {
        return productRepository.findTop5ProductsBySales();
    }

    public Page<Product> getAllProductList(Pageable pageable) {
        return productRepository.findAllProduct(pageable);
    }

    public Page<Product> getAllProductListByNutrientId(Long nutrientId, Pageable pageable) {
        return productRepository.findAllProductByNutrientId(nutrientId, pageable);
    }

    public Page<Product> getAllProductListByCategoryId(Long categoryId, Pageable pageable) {
        return productRepository.findAllProductByCategoryId(categoryId, pageable);
    }

    public Page<Product> getAllProductBySellerId(Long sellerId, Pageable pageable) {
        return productRepository.findAllProductBySellerId(sellerId, pageable);
    }

    @Transactional
    public RsData<Product> like(Member member, Long productId) {
        Product product = productRepository.findById(productId).orElse(null);
        if (product == null) {
            return RsData.of("F-1", "존재하지 않는 상품입니다.");
        }
        product.addLikedMember(member);
        publisher.publishEvent(new EventAfterLike(this, member, product));
        return RsData.of("S-1", "상품 좋아요");
    }

    @Transactional
    public RsData<Product> unlike(Member member, Long productId) {
        Product product = productRepository.findById(productId).orElse(null);
        if (product == null) {
            return RsData.of("F-1", "존재하지 않는 상품입니다.");
        }
        product.deleteLikedMember(member);
        publisher.publishEvent(new EventAfterUnlike(this, member, product));
        return RsData.of("S-1", "상품 좋아요 취소");
    }
}
