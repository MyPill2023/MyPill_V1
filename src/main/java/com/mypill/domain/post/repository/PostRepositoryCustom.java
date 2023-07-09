package com.mypill.domain.post.repository;

import com.mypill.domain.post.dto.PostPagingResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PostRepositoryCustom {
    Page<PostPagingResponse> findPostsWithMembers(Pageable pageable);

    Page<PostPagingResponse> findPostsWithMembersAndTitleContaining(String keyword, Pageable pageable);

    Page<PostPagingResponse> findPostsWithMembersAndContentContaining(String keyword, Pageable pageable);
}
