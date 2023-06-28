package com.mypill.domain.product.repository;

import com.mypill.domain.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByDeleteDateIsNull();
    List<Product> findByNutrientsIdAndDeleteDateIsNull(Long nutrientId);
    List<Product> findByCategoriesIdAndDeleteDateIsNull(Long categoryId);
}
