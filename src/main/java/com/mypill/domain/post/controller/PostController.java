package com.mypill.domain.post.controller;

import com.mypill.domain.post.dto.PostRequest;
import com.mypill.domain.post.entity.Post;
import com.mypill.domain.post.service.PostService;
import com.mypill.global.rq.Rq;
import com.mypill.global.rsData.RsData;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/usr/post")
@RequiredArgsConstructor
@Controller
@Tag(name = "PostController", description = "게시판")
public class PostController {
    private final PostService postService;
    private final Rq rq;

    @GetMapping("/list")
    @Operation(summary = "게시글 목록")
    public String showList(Model model) {
        List<Post> posts = postService.getList();
        model.addAttribute("posts", posts);
        return "usr/post/list";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/create")
    @Operation(summary = "게시글 등록 폼")
    public String create() {
        return "usr/post/create";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/create")
    @Operation(summary = "게시글 등록")
    public String create(@Valid PostRequest postRequest) {
        RsData<Post> createRsData = postService.create(postRequest, rq.getMember());
        if (createRsData.isFail()) {
            return rq.historyBack(createRsData.getMsg());
        }
        return rq.redirectWithMsg("/usr/post/detail/%s".formatted(createRsData.getData().getId()), createRsData);
    }

    @GetMapping("/detail/{postId}")
    @Operation(summary = "게시글 상세")
    public String showPost(@PathVariable Long postId, Model model) {
        Post post = postService.findById(postId).orElse(null);
        if (post == null) {
            return rq.historyBack("존재하지 않는 게시글입니다.");
        }
        if (post.getDeleteDate() != null) {
            return rq.historyBack("삭제된 게시글입니다.");
        }
        model.addAttribute("post", post);
        return "usr/post/detail";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/update/{postId}")
    @Operation(summary = "게시글 수정 폼")
    public String update(@PathVariable Long postId, Model model) {
        Post post = postService.findById(postId).orElse(null);
        if (post == null) {
            return rq.historyBack("존재하지 않는 게시글입니다.");
        }
        if (post.getPoster().getId() != rq.getMember().getId()) {
            return rq.historyBack("작성자만 수정이 가능합니다.");
        }
        model.addAttribute("post", post);
        return "usr/post/update";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/update/{postId}")
    @Operation(summary = "게시글 수정")
    public String update(@PathVariable Long postId, @Valid PostRequest postRequest) {
        RsData<Post> updateRsData = postService.update(postId, postRequest, rq.getMember());
        if (updateRsData.isFail()) {
            return rq.historyBack(updateRsData.getMsg());
        }
        return rq.redirectWithMsg("/usr/post/detail/%s".formatted(updateRsData.getData().getId()), updateRsData);
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/delete/{postId}")
    @Operation(summary = "게시글 삭제")
    public String delete(@PathVariable Long postId) {
        RsData<Post> deleteRsData = postService.delete(postId, rq.getMember());
        if (deleteRsData.isFail()) {
            return rq.historyBack(deleteRsData.getMsg());
        }
        return rq.redirectWithMsg("/usr/post/list", deleteRsData);
    }

    @PostMapping("/search")
    public String search(String keyword, String searchType, Model model) {
        List<Post> posts = null;
        if (searchType.equals("title")) {
            posts = postService.searchTitle(keyword);
        }
        if (searchType.equals("content")) {
            posts = postService.searchContent(keyword);
        }
        model.addAttribute("posts", posts);
        return "usr/post/list";
    }
}
