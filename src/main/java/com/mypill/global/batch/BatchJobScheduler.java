package com.mypill.global.batch;

import com.mypill.domain.comment.entity.Comment;
import com.mypill.domain.comment.service.CommentService;
import com.mypill.domain.member.entity.Member;
import com.mypill.domain.member.service.MemberService;
import com.mypill.domain.post.entity.Post;
import com.mypill.domain.post.service.PostService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BatchJobScheduler {
    private final MemberService memberService;
    private final PostService postService;
    private final CommentService commentService;

    public BatchJobScheduler(MemberService memberService, PostService postService, CommentService commentService) {
        this.memberService = memberService;
        this.postService = postService;
        this.commentService = commentService;
    }

    @Scheduled(cron = "0 1 4 * * *")
    public void deleteUnverifiedMembers() {
        List<Member> unverifiedMembers = memberService.getUnverifiedMember();
        for (Member member : unverifiedMembers) {
            memberService.deleteMember(member);
        }
    }

    @Scheduled(cron = "0 2 4 * * *")
    public void deletePosts() {
        List<Post> deletedPosts = postService.getDeletedPosts();
        for (Post post : deletedPosts) {
            postService.hardDelete(post);
        }
    }

    @Scheduled(cron = "0 1 4 * * *")
    public void deleteComments() {
        List<Comment> deletedComments = commentService.getDeletedComments();
        for (Comment comment : deletedComments) {
            commentService.deleteComment(comment);
        }
    }
}