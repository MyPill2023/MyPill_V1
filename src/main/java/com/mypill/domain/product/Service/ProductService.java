package com.mypill.domain.product.Service;

import com.mypill.domain.category.entity.Category;
import com.mypill.domain.category.service.CategoryService;
import com.mypill.domain.member.entity.Member;
import com.mypill.domain.member.service.MemberService;
import com.mypill.domain.nutrient.Service.NutrientService;
import com.mypill.domain.nutrient.entity.Nutrient;
import com.mypill.domain.product.dto.request.ProductRequest;
import com.mypill.domain.product.dto.response.ProductResponse;
import com.mypill.domain.product.entity.Product;
import com.mypill.domain.product.repository.ProductRepository;
import com.mypill.global.rq.Rq;
import com.mypill.global.rsData.RsData;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final NutrientService nutrientService;
    private final CategoryService categoryService;
    private final MemberService memberService;
    private final Rq rq;

    @Transactional
    public RsData<ProductResponse> create(ProductRequest request) {

        List<Nutrient> nutrients = MappingNutrient(request.getNutrients());
        List<Category> categories = MappingCategory(request.getCategories());

        Member seller = memberService.findById(request.getSellerId()).orElse(null);
        Product product = Product.of(request, nutrients, categories, seller);

        productRepository.save(product);

        return RsData.of("S-1", "상품 등록이 완료되었습니다.", ProductResponse.of(product));
    }

    public RsData<ProductResponse> get(Long productId){
        Product product = findById(productId).orElse(null);

        if(product == null){
            return RsData.of("F-1", "존재하지 않는 상품입니다.");
        }

        return RsData.of("S-1", "존재하는 상품입니다.", ProductResponse.of(product));
    }

    public List<ProductResponse> getAllProduct(List<Product> products){
        return products.stream().map(this::convertToResponse).collect(Collectors.toList());
    }

    @Transactional
    public RsData<Product> update(Long productId, ProductRequest request) {

        Product product = findById(productId).orElse(null);
        if(product == null){
            return RsData.of("F-1", "존재하지 않는 상품입니다.");
        }

        if(!hasPermisson(product.getSeller().getId())){
            return RsData.of("F-2", "수정 권한이 없습니다.");
        }

        List<Nutrient> nutrients = MappingNutrient(request.getNutrients());
        List<Category> categories = MappingCategory(request.getCategories());
        product.update(request, nutrients, categories);

        return RsData.of("S-1", "상품 수정이 완료되었습니다.", product);
    }

    @Transactional
    public RsData<Product> delete(Long productId) {

        Product product = findById(productId).orElse(null);
        if(product == null){
            return RsData.of("F-1", "존재하지 않는 상품입니다.");
        }

        if(!hasPermisson(product.getSeller().getId())){
            return RsData.of("F-2", "삭제 권한이 없습니다.");
        }

        product = product.toBuilder().deleteDate(LocalDateTime.now()).build();
        productRepository.save(product);

        return RsData.of("S-1", "상품 삭제가 완료되었습니다.", product);
    }

    public Optional<Product> findById(Long productId){
        return productRepository.findById(productId);
    }

    public List<Product> findAll() {
        return productRepository.findAll();
    }

    public List<Product> findNotDeleted() {
        return productRepository.findByDeleteDateIsNull();
    }

    public List<Product> findByNutrientsId(Long nutrientId) {
        return productRepository.findByNutrientsIdAndDeleteDateIsNull(nutrientId);
    }

    public List<Product> findByCategoriesId(Long categoryId) {
        return productRepository.findByCategoriesIdAndDeleteDateIsNull(categoryId);
    }

    private  List<Nutrient> MappingNutrient(List<Nutrient> nutrients){
        return nutrientService.findByIdIn(nutrients.stream()
                .map(Nutrient::getId)
                .collect(Collectors.toList()));
    }
    private  List<Category> MappingCategory(List<Category> categories){
        return categoryService.findByIdIn(categories.stream()
                .map(Category::getId)
                .collect(Collectors.toList()));
    }

    private ProductResponse convertToResponse(Product product){
        return ProductResponse.of(product);
    }

    private boolean hasPermisson(Long sellerId) {
        return sellerId.equals(rq.getMember().getId());
    }
}
