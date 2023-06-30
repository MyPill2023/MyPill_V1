package com.mypill.domain.consumer.controller;

import com.mypill.domain.comment.entity.Comment;
import com.mypill.domain.comment.service.CommentService;
import com.mypill.domain.member.service.MemberService;
import com.mypill.domain.post.entity.Post;
import com.mypill.domain.post.service.PostService;
import com.mypill.global.rq.Rq;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/usr/consumer")
public class ConsumerController {
    private final MemberService memberService;
    private final PostService postService;
    private final CommentService commentService;
    private final Rq rq;

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/main")
    public String main() {
        return "usr/consumer/main";
    }
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/myInfo")
    public String myInfo() {
        return "usr/consumer/myInfo";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/myLikes")
    public String myLikes() {
        return "usr/consumer/myLikes";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/myPosts")
    public String myPosts(Model model) {
        List<Post> posts = postService.getList(rq.getMember());
        model.addAttribute("posts", posts);
        return "usr/consumer/myPosts";
    }
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/myComments")
    public String myComments(Model model) {
        List<Comment> comments = commentService.getList(rq.getMember());
        model.addAttribute("comments", comments);
        return "usr/consumer/myComments";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/mySchedule")
    public String mySchedule() {
        return "usr/consumer/mySchedule";
    }

    @PreAuthorize("isAuthenticated()")
    @ResponseBody
    @PostMapping("/name/update")
    public String nameUpdate(String newName) {
        return memberService.nameUpdate(rq.getMember(), newName);
    }
}
