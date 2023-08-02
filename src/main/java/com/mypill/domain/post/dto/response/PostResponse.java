package com.mypill.domain.post.dto.response;

import com.mypill.domain.member.entity.Member;
import com.mypill.domain.post.entity.Post;
import lombok.*;

@Data
@Builder
public class PostResponse {
    private Post post;
    private Member poster;

    public static PostResponse of(Post post, Member poster){
        return PostResponse.builder()
                .post(post)
                .poster(poster)
                .build();
    }
}
