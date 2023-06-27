package com.mypill.domain.comment.controller;

import com.mypill.domain.comment.dto.CommentRequest;
import com.mypill.domain.comment.entity.Comment;
import com.mypill.domain.comment.service.CommentService;
import com.mypill.global.rq.Rq;
import com.mypill.global.rsData.RsData;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;


@RequestMapping("/usr/comment")
@RequiredArgsConstructor
@Controller
@Tag(name = "CommentController", description = "댓글")
public class CommentController {

    private final CommentService commentService;
    private final Rq rq;

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/create/{postId}")
    @Operation(summary = "댓글 등록")
    public String create(@PathVariable String postId, @Valid CommentRequest commentRequest) {
        RsData<Comment> commentRsData = commentService.create(commentRequest, rq.getMember(), postId);
        if (commentRsData.isFail()) {
            return rq.historyBack(commentRsData.getMsg());
        }
        return rq.redirectWithMsg("/usr/post/detail/%s".formatted(postId), commentRsData);
    }

//    @PreAuthorize("isAuthenticated()")
//    @PostMapping("/update/{postId}")
//    @Operation(summary = "댓글 수정")
//    public String update(@PathVariable Long postId, @Valid PostRequest postRequest) {
//        if (rq.isLogout()) {
//            return rq.historyBack("로그인 후 이용 가능합니다.");
//        }
//        RsData<Post> updateRsData = postService.update(postId, postRequest, rq.getMember());
//        if (updateRsData.isFail()) {
//            return rq.historyBack(updateRsData.getMsg());
//        }
//        return rq.redirectWithMsg("/usr/post/detail/%s".formatted(updateRsData.getData().getId()), updateRsData);
//    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/delete/{postId}/{commentId}")
    @Operation(summary = "댓글 삭제")
    public String delete(@PathVariable("postId") String postId, @PathVariable("commentId") String commentId) {
        RsData<Comment> commentRsData = commentService.delete(rq.getMember(), commentId);
        if (commentRsData.isFail()) {
            return rq.historyBack(commentRsData.getMsg());
        }
        return rq.redirectWithMsg("/usr/post/detail/%s".formatted(postId), commentRsData);
    }
}
