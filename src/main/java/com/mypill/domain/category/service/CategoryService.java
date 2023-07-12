package com.mypill.domain.category.service;

import com.mypill.domain.category.entity.Category;
import com.mypill.domain.category.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public List<Category> findAll() {
        return categoryRepository.findAll();
    }

    public List<Category> findAllByOrderByNameAsc() {
        return categoryRepository.findAllByOrderByNameAsc();
    }

    public List<Category> findByIdIn(List<Long> categoryIds) {
        return categoryRepository.findByIdIn(categoryIds);
    }

    public Optional<Category> findById(Long categoryId) {
        if (categoryRepository.findById(categoryId).isEmpty()) {
            return Optional.empty();
        }
        return categoryRepository.findById(categoryId);
    }
}