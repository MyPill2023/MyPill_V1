package com.mypill.domain.comment.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CommentResponse {
    private String responseCode;
    private String errorMsg;
    private String newContent;
}
