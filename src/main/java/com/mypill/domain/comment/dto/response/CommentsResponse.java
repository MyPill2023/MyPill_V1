package com.mypill.domain.comment.dto.response;

import com.mypill.domain.comment.entity.Comment;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class CommentsResponse {
    private List<Comment> comments;

    public static CommentsResponse of(List<Comment> comments) {
        return new CommentsResponse(comments);
    }
}
