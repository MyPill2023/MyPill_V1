package com.mypill.domain.comment.repository;

import com.mypill.domain.post.dto.CommentResponse;
import com.mypill.domain.post.dto.PostResponse;
import com.mypill.domain.product.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CommentRepositoryCustom {

    List<CommentResponse> findCommentsWithMembers(Long postId);

}
