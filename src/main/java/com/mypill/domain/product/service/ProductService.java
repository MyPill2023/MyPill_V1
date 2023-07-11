package com.mypill.domain.product.service;

import com.mypill.domain.image.entity.Image;
import com.mypill.domain.image.service.ImageService;
import com.mypill.domain.category.entity.Category;
import com.mypill.domain.category.service.CategoryService;
import com.mypill.domain.member.entity.Member;
import com.mypill.domain.member.service.MemberService;
import com.mypill.domain.nutrient.service.NutrientService;
import com.mypill.domain.nutrient.entity.Nutrient;
import com.mypill.domain.product.dto.request.ProductRequest;
import com.mypill.domain.product.entity.Product;
import com.mypill.domain.product.repository.ProductRepository;
import com.mypill.global.aws.s3.dto.AmazonS3Dto;
import com.mypill.global.event.EventAfterLike;
import com.mypill.global.event.EventAfterUnlike;
import com.mypill.global.rsdata.RsData;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final NutrientService nutrientService;
    private final CategoryService categoryService;
    private final MemberService memberService;
    private final ApplicationEventPublisher publisher;
    private final ImageService imageService;

    //NotProd용
    @Transactional
    public RsData<Product> create(ProductRequest request) {

        List<Nutrient> nutrients = nutrientService.findByIdIn(request.getNutrientIds());
        List<Category> categories = categoryService.findByIdIn(request.getCategoryIds());

        Member seller = memberService.findById(request.getSellerId()).orElse(null);

        Product product = Product.of(request, nutrients, categories, seller, new HashSet<>());

        productRepository.save(product);
        return RsData.of("S-1", "상품 등록이 완료되었습니다.", product);
    }


    @Transactional
    public RsData<Product> create(ProductRequest request, MultipartFile multipartFile) {

        List<Nutrient> nutrients = nutrientService.findByIdIn(request.getNutrientIds());
        List<Category> categories = categoryService.findByIdIn(request.getCategoryIds());

        Member seller = memberService.findById(request.getSellerId()).orElse(null);

        Product product = Product.of(request, nutrients, categories, seller, new HashSet<>());

        if (!multipartFile.isEmpty()) {
            AmazonS3Dto amazonS3ImageDto = imageService.saveImageOnServer(multipartFile, product);
            Image image = new Image(amazonS3ImageDto, multipartFile, product);
            product.addImage(image);
        }
        productRepository.save(product);
        return RsData.of("S-1", "상품 등록이 완료되었습니다.", product);
    }


    public RsData<Product> get(Long productId) {
        Product product = findById(productId).orElse(null);

        if (product == null) {
            return RsData.of("F-1", "존재하지 않는 상품입니다.");
        }

        return RsData.of("S-1", "존재하는 상품입니다.", product);
    }

    //test용
    @Transactional
    public RsData<Product> update(Member actor, Long productId, ProductRequest request) {

        Product product = findById(productId).orElse(null);
        if (product == null) {
            return RsData.of("F-1", "존재하지 않는 상품입니다.");
        }

        if (!actor.getId().equals(product.getSeller().getId())) {
            return RsData.of("F-2", "수정 권한이 없습니다.");
        }

        List<Nutrient> nutrients = nutrientService.findByIdIn(request.getNutrientIds());
        List<Category> categories = categoryService.findByIdIn(request.getCategoryIds());

//        product.update(request, nutrients, categories);

        return RsData.of("S-1", "상품 수정이 완료되었습니다.", product);
    }


    @Transactional
    public RsData<Product> update(Member actor, Long productId, ProductRequest request, MultipartFile multipartFile) {

        Product product = findById(productId).orElse(null);
        if (product == null) {
            return RsData.of("F-1", "존재하지 않는 상품입니다.");
        }

        if (!actor.getId().equals(product.getSeller().getId())) {
            return RsData.of("F-2", "수정 권한이 없습니다.");
        }

        List<Nutrient> nutrients = nutrientService.findByIdIn(request.getNutrientIds());
        List<Category> categories = categoryService.findByIdIn(request.getCategoryIds());

        if (!multipartFile.isEmpty()) {
            AmazonS3Dto amazonS3ImageDto = imageService.updateImageOnServer(multipartFile, product);
            Image image = product.getImage();
            if (image == null) {
                product.addImage(new Image(amazonS3ImageDto, multipartFile, product));
            } else {
                product.getImage().update(amazonS3ImageDto, multipartFile);
            }
        }
        product.update(request, nutrients, categories);
        return RsData.of("S-1", "상품 수정이 완료되었습니다.", product);
    }

    @Transactional
    public RsData<Product> delete(Member actor, Long productId) {

        Product product = findById(productId).orElse(null);
        if (product == null) {
            return RsData.of("F-1", "존재하지 않는 상품입니다.");
        }

        if (!actor.getId().equals(product.getSeller().getId())) {
            return RsData.of("F-2", "삭제 권한이 없습니다.", product);
        }

        product = product.toBuilder().deleteDate(LocalDateTime.now()).build();
        productRepository.save(product);

        return RsData.of("S-1", "상품 삭제가 완료되었습니다.", product);
    }

    public Optional<Product> findById(Long productId) {
        return productRepository.findById(productId);
    }

    public List<Product> findAll() {
        return productRepository.findAll();
    }

    public Page<Product> getAllProductList(Pageable pageable) {
        return productRepository.findAllProduct(pageable);
    }

    public List<Product> findTop5ProductsBySales() {
        return productRepository.findTop5ProductsBySales();
    }

    public Page<Product> getAllProductListByNutrientId(Long nutrientId, Pageable pageable) {
        return productRepository.findAllProductByNutrientId(nutrientId, pageable);
    }

    public Page<Product> getAllProductListByCategoryId(Long categoryId, Pageable pageable) {
        return productRepository.findAllProductByCategoryId(categoryId, pageable);
    }

    @Transactional
    public int like(Member member, Long productId) {
        if (member == null) {
            return 1;
        }
        Product product = productRepository.findById(productId).orElse(null);
        if (product == null) {
            return 2;
        }
        product.addLikedMember(member);
        productRepository.save(product);
        publisher.publishEvent(new EventAfterLike(this, member, product));
        return 0;
    }

    @Transactional
    public int unlike(Member member, Long productId) {
        if (member == null) {
            return 1;
        }
        Product product = productRepository.findById(productId).orElse(null);
        if (product == null) {
            return 2;
        }
        product.deleteLikedMember(member);
        productRepository.save(product);
        publisher.publishEvent(new EventAfterUnlike(this, member, product));
        return 0;
    }
}
