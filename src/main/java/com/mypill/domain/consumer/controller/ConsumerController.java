package com.mypill.domain.consumer.controller;

import com.mypill.domain.comment.entity.Comment;
import com.mypill.domain.comment.service.CommentService;
import com.mypill.domain.consumer.service.ConsumerService;
import com.mypill.domain.post.entity.Post;
import com.mypill.domain.post.service.PostService;
import com.mypill.global.rq.Rq;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/usr/consumer")
public class ConsumerController {
    private final ConsumerService consumerService;
    private final PostService postService;
    private final CommentService commentService;
    private final Rq rq;

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/myPage")
    public String showMyPage() {
        return "usr/consumer/myPage";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/myLikes")
    public String myLikes() {
        return "usr/consumer/myLikes";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/myPost")
    public String myPost(Model model) {
        List<Post> posts = postService.getList(rq.getMember());
        List<Comment> comments = commentService.getList(rq.getMember());
        model.addAttribute("posts", posts);
        model.addAttribute("comments", comments);
        return "usr/consumer/myPost";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/mySchedule")
    public String mySchedule() {
        return "usr/consumer/mySchedule";
    }
}
