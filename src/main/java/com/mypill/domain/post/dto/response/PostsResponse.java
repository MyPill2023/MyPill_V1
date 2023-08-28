package com.mypill.domain.post.dto.response;

import com.mypill.domain.post.entity.Post;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class PostsResponse {
    private List<PostResponse> posts;

    public static PostsResponse of(List<Post> posts) {
        return PostsResponse.builder()
                .posts(posts.stream().map(PostResponse::of).toList())
                .build();
    }
}
