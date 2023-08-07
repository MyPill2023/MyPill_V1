package com.mypill.domain.post.dto.response;

import com.mypill.domain.post.entity.Post;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class PostsResponse {
    private List<Post> posts;

    public static PostsResponse of(List<Post> posts) {
        return new PostsResponse(posts);
    }
}
