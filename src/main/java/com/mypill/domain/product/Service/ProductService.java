package com.mypill.domain.product.Service;

import com.mypill.domain.product.dto.request.ProductRequest;
import com.mypill.domain.product.dto.response.ProductResponse;
import com.mypill.domain.product.entity.Product;
import com.mypill.domain.product.repository.ProductRepository;
import com.mypill.global.rsData.RsData;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    @Transactional
    public RsData<ProductResponse> create(ProductRequest request) {
        Product product = Product.of(request);
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
    public RsData<ProductResponse> update(Long productId, ProductRequest request) {

        Product product = findById(productId).orElse(null);
        if(product == null){
            return RsData.of("F-1", "존재하지 않는 상품입니다.", ProductResponse.of(product));
        }

        product = product.toBuilder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .stock(request.getStock())
                .nutrients(request.getNutrients())
                .categories(request.getCategories())
                .build();

        productRepository.save(product);
        return RsData.of("S-1", "상품 수정이 완료되었습니다.", ProductResponse.of(product));
    }

    @Transactional
    public RsData<ProductResponse> delete(Long productId) {

        Product product = findById(productId).orElse(null);
        if(product == null){
            return RsData.of("F-1", "존재하지 않는 상품입니다.");
        }
        productRepository.delete(product);
        return RsData.of("S-1", "상품 삭제가 완료되었습니다.", ProductResponse.of(product));
    }

    public Optional<Product> findById(Long productId){
        return productRepository.findById(productId);
    }

    public List<Product> findAll() {
        return productRepository.findAll();
    }

    private ProductResponse convertToResponse(Product product){
        return ProductResponse.of(product);
    }
}
