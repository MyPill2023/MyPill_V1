package com.mypill.domain.comment.repository;

import com.mypill.domain.comment.dto.response.CommentResponse;

import java.util.List;

public interface CommentRepositoryCustom {

    List<CommentResponse> findCommentsWithMembers(Long postId);
}
