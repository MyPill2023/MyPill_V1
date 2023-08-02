package com.mypill.domain.comment.controller;

import com.mypill.domain.comment.dto.request.CommentRequest;
import com.mypill.domain.comment.entity.Comment;
import com.mypill.domain.comment.service.CommentService;
import com.mypill.global.rq.Rq;
import com.mypill.global.rsdata.RsData;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;


@RequestMapping("/comment")
@RequiredArgsConstructor
@Controller
@Tag(name = "CommentController", description = "댓글")
public class CommentController {

    private final CommentService commentService;
    private final Rq rq;

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/create/{postId}")
    @Operation(summary = "댓글 등록")
    public String create(@PathVariable Long postId, @Valid CommentRequest commentRequest) {
        RsData<Comment> commentRsData = commentService.create(commentRequest, rq.getMember(), postId);
        if (commentRsData.isFail()) {
            return rq.historyBack(commentRsData);
        }
        return rq.redirectWithMsg("/post/detail/%s".formatted(postId), commentRsData);
    }

    @PreAuthorize("isAuthenticated()")
    @ResponseBody
    @PostMapping("/update/{commentId}")
    @Operation(summary = "댓글 수정")
    public RsData<String> update(CommentRequest commentRequest, @PathVariable Long commentId) {
        return commentService.update(commentRequest, rq.getMember(), commentId);
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/delete/{postId}/{commentId}")
    @Operation(summary = "댓글 삭제")
    public String delete(@PathVariable Long postId, @PathVariable Long commentId) {
        RsData<Comment> deleteRsData = commentService.softDelete(rq.getMember(), commentId);
        if (deleteRsData.isFail()) {
            return rq.historyBack(deleteRsData);
        }
        return rq.redirectWithMsg("/post/detail/%s".formatted(postId), deleteRsData);
    }
}
