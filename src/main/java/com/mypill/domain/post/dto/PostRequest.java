package com.mypill.domain.post.dto;

import com.mypill.domain.post.entity.Post;
import lombok.Data;

@Data
public class PostRequest {

    private String title;
    private String content;
//    private MultipartFile image;

    public Post toEntity() {
        return Post.builder()
                .title(title)
                .content(content)
                .build();
    }
}
