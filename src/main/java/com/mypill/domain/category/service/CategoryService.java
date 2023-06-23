package com.mypill.domain.category.service;

import com.mypill.domain.category.entity.Category;
import com.mypill.domain.category.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;

    public List<Category> findAll(){
        return categoryRepository.findAll();
    }
    public List<Category> findAllByOrderByNameAsc(){
        return categoryRepository.findAllByOrderByNameAsc();
    }
}