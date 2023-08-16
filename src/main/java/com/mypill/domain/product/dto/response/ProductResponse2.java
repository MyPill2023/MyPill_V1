package com.mypill.domain.product.dto.response;

import com.mypill.domain.image.entity.Image;
import com.mypill.domain.product.entity.Product;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;


@Data
@Builder
@AllArgsConstructor
public class ProductResponse2 {
    private Long productId;
    private Image image;
    private String sellerName;
    private String name;
    private int likedMemberSize;
    private Long price;

    public static ProductResponse2 of(Product product){
        return ProductResponse2.builder()
                .productId(product.getId())
                .image(product.getImage())
                .sellerName(product.getSeller().getName())
                .name(product.getName())
                .likedMemberSize(product.getLikedMembers().size())
                .price(product.getPrice())
                .build();
    }
}
