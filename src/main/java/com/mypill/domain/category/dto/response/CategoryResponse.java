package com.mypill.domain.category.dto.response;

import com.mypill.domain.category.entity.Category;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CategoryResponse {

    private Long id;
    private String name;

    public static CategoryResponse of(Category category){
        return new CategoryResponse(category.getId(), category.getName());
    }

}
