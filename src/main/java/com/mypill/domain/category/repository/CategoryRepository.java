package com.mypill.domain.category.repository;

import com.mypill.domain.category.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findAllByOrderByNameAsc();
    List<Category> findByIdIn(List<Long> categoryIds);
}
