package com.mypill.domain.post.controller;

import com.mypill.domain.post.dto.PostCreateRequest;
import com.mypill.domain.post.entity.Post;
import com.mypill.domain.post.service.PostService;
import com.mypill.global.rq.Rq;
import com.mypill.global.rsData.RsData;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@RequestMapping("/usr/post")
@RequiredArgsConstructor
@Controller
@Tag(name = "QuestionController", description = "게시판")
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

    @GetMapping("/create")
    @Operation(summary = "게시글 등록 폼")
    public String create() {
        return "usr/post/create";
    }

    @GetMapping("/detail/{postId}")
    @Operation(summary = "게시글 상세")
    public String showPost(@PathVariable Long postId, Model model) {
        model.addAttribute("post", postService.get(postId).getData());
        return "usr/post/detail";
    }

    @PostMapping("/create")
    @Operation(summary = "게시글 등록")
    public String create(@Valid PostCreateRequest postCreateRequest) {
        RsData<Post> createRsData = postService.create(postCreateRequest);
        if (createRsData.isFail()) {
            return rq.historyBack(createRsData.getMsg());
        }
        return rq.redirectWithMsg("/usr/post/detail/%s".formatted(createRsData.getData().getId()), createRsData);
    }
}
