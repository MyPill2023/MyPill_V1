package com.mypill.domain.product.Service;

import com.mypill.domain.product.dto.request.ProductRequestDto;
import com.mypill.domain.product.dto.response.ProductResponseDto;
import com.mypill.domain.product.entity.Product;
import com.mypill.domain.product.repository.ProductRepository;
import com.mypill.global.rsData.RsData;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    private ProductRepository productRepository;

    @Transactional
    public ProductResponseDto create(ProductRequestDto requestDto) {
        Product product = Product.of(requestDto);
        productRepository.save(product);
        return ProductResponseDto.of(product);
    }


    public ProductResponseDto get(Long productId){
        Product product = findById(productId).orElse(null);
        return ProductResponseDto.of(product);
    }


    @Transactional
    public RsData<ProductResponseDto> update(Long productId, ProductRequestDto requestDto) {

        Product product = findById(productId).orElse(null);
        if(product == null){
            return RsData.of("F-1", "존재하지 않는 상품입니다.", ProductResponseDto.of(product));
        }

        product = product.toBuilder()
                .name(requestDto.getName())
                .description(requestDto.getDescription())
                .price(requestDto.getPrice())
                .stock(requestDto.getStock())
                .nutrients(requestDto.getNutrients())
                .build();

        productRepository.save(product);
        return RsData.of("S-1", "상품 수정이 완료되었습니다.", ProductResponseDto.of(product));
    }

    @Transactional
    public RsData<ProductResponseDto> delete(Long productId) {

        Product product = findById(productId).orElse(null);
        if(product == null){
            return RsData.of("F-1", "존재하지 않는 상품입니다.");
        }
        productRepository.delete(product);
        return RsData.of("S-1", "상품 수정이 완료되었습니다.", ProductResponseDto.of(product));
    }


    public Optional<Product> findById(Long productId){
        return productRepository.findById(productId);
    }

    public List<Product> findAll() {
        return productRepository.findAll();
    }
}
