package com.mypill.domain.post.controller;

import com.mypill.domain.comment.dto.CommentResponse;
import com.mypill.domain.comment.service.CommentService;
import com.mypill.domain.post.dto.response.PostResponse;
import com.mypill.domain.post.dto.request.PostRequest;
import com.mypill.domain.post.entity.Post;
import com.mypill.domain.post.service.PostService;
import com.mypill.global.rq.Rq;
import com.mypill.global.rsdata.RsData;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/post")
@RequiredArgsConstructor
@Controller
@Tag(name = "PostController", description = "게시판")
public class PostController {
    private final PostService postService;
    private final CommentService commentService;
    private final Rq rq;

    @GetMapping("/list")
    @Operation(summary = "게시글 목록 페이지")
    public String showList(String keyword, String searchType,
                           @RequestParam(defaultValue = "0") int pageNumber,
                           @RequestParam(defaultValue = "10") int pageSize,
                           Model model) {
        Page<PostResponse> pageResult = postService.getPosts(keyword, searchType, pageNumber, pageSize);
        List<PostResponse> postResponse = pageResult.getContent();
        model.addAttribute("page", pageResult);
        model.addAttribute("postResponses", postResponse);
        return "usr/post/list";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/create")
    @Operation(summary = "게시글 작성 페이지")
    public String showCreateForm() {
        return "usr/post/create";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/create")
    @Operation(summary = "게시글 작성")
    public String create(@Valid PostRequest postRequest) {
        Post post = postService.create(postRequest, rq.getMember());
        return rq.redirectWithMsg("/post/detail/%s".formatted(post.getId()), "게시글 작성이 완료되었습니다.");
    }

    @GetMapping("/detail/{postId}")
    @Operation(summary = "게시글 상세 페이지")
    public String showPost(@PathVariable Long postId, Model model) {
        RsData<PostResponse> postResponseRsData = postService.showDetail(postId);
        if (postResponseRsData.isFail()) {
            return rq.historyBack(postResponseRsData);
        }
        List<CommentResponse> commentResponses = commentService.getCommentsWithMembers(postId);
        model.addAttribute("postResponse", postResponseRsData.getData());
        model.addAttribute("commentResponses", commentResponses);
        return "usr/post/detail";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/update/{postId}")
    @Operation(summary = "게시글 수정 페이지")
    public String showUpdateForm(@PathVariable Long postId, Model model) {
        RsData<Post> post = postService.beforeUpdate(postId, rq.getMember().getId());
        if (post.isFail()) {
            return rq.historyBack(post);
        }
        model.addAttribute("postResponse", PostResponse.of(post.getData(), rq.getMember()));
        return "usr/post/update";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/update/{postId}")
    @Operation(summary = "게시글 수정")
    public String update(@PathVariable Long postId, @Valid PostRequest postRequest) {
        RsData<Post> updateRsData = postService.update(postId, postRequest, rq.getMember().getId());
        if (updateRsData.isFail()) {
            return rq.historyBack(updateRsData);
        }
        return rq.redirectWithMsg("/post/detail/%s".formatted(updateRsData.getData().getId()), updateRsData);
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/delete/{postId}")
    @Operation(summary = "게시글 삭제")
    public String delete(@PathVariable Long postId) {
        RsData<Post> deleteRsData = postService.softDelete(postId, rq.getMember());
        if (deleteRsData.isFail()) {
            return rq.historyBack(deleteRsData);
        }
        return rq.redirectWithMsg("/post/list", deleteRsData);
    }
}
