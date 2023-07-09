package com.mypill.domain.post.repository;

import com.mypill.domain.post.dto.PostResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PostRepositoryCustom {
    Page<PostResponse> findPostsWithMembers(Pageable pageable);

    Page<PostResponse> findPostsWithMembersAndTitleContaining(String keyword, Pageable pageable);

    Page<PostResponse> findPostsWithMembersAndContentContaining(String keyword, Pageable pageable);
}
