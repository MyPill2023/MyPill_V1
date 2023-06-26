package com.mypill.domain.post.dto;

import com.mypill.domain.post.entity.Post;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class PostCreateRequest {

    private String title;
    private String content;
//    private MultipartFile image;

    public Post toEntity() {
        return Post.builder()
                .title(title)
                .content(content)
                .answerCnt(0L)
                .build();
    }
}
