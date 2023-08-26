package com.mypill.domain.nutrient.dto.response;

import com.mypill.domain.nutrient.entity.Nutrient;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class NutrientResponse {
    private Long id;
    private String name;
    private String description;

    public static NutrientResponse of(Nutrient nutrient){
        return new NutrientResponse(nutrient.getId(), nutrient.getName(), nutrient.getDescription());
    }
}
