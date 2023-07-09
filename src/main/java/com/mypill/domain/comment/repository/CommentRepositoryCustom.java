package com.mypill.domain.comment.repository;

import com.mypill.domain.comment.dto.CommentResponse;

import java.util.List;

public interface CommentRepositoryCustom {

    List<CommentResponse> findCommentsWithMembers(Long postId);

}
