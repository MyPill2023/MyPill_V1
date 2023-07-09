package com.mypill.domain.post.dto;

import com.mypill.domain.category.entity.Category;
import com.mypill.domain.member.entity.Member;
import com.mypill.domain.nutrient.entity.Nutrient;
import com.mypill.domain.post.entity.Post;
import com.mypill.domain.product.entity.Product;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@Getter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostResponse {
    private Long id;
    private String title;
    private String content;
    private Member poster;

    public static PostResponse of(Post post, Member member) {
        return PostResponse.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .poster(member)
                .build();
    }
}
