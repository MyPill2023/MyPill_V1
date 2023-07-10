package com.mypill.domain.comment.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CommentAJAXResponse {
    private String responseCode;
    private String errorMsg;
    private String newContent;
}
