package com.mypill.domain.product.repository;

import com.mypill.domain.product.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ProductRepositoryCustom {

    List<Product> findTop5ProductsBySales();

    Page<Product> findAllProduct(Pageable pageable);

    Page<Product> findAllProductByNutrientId(Long nutrientId, Pageable pageable);

    Page<Product> findAllProductByCategoryId(Long categoryId, Pageable pageable);
}
