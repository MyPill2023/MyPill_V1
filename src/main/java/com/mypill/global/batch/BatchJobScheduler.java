package com.mypill.global.batch;

import com.mypill.domain.comment.entity.Comment;
import com.mypill.domain.comment.service.CommentService;
import com.mypill.domain.diary.entity.Diary;
import com.mypill.domain.diary.service.DiaryService;
import com.mypill.domain.member.entity.Member;
import com.mypill.domain.member.service.MemberService;
import com.mypill.domain.notification.entity.Notification;
import com.mypill.domain.notification.service.NotificationService;
import com.mypill.domain.post.entity.Post;
import com.mypill.domain.post.service.PostService;
import com.mypill.global.event.EventBeforeDiaryCheck;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Component
public class BatchJobScheduler {
    private final MemberService memberService;
    private final PostService postService;
    private final CommentService commentService;
    private final DiaryService diaryService;
    private final ApplicationEventPublisher publisher;

    public BatchJobScheduler(MemberService memberService, PostService postService, CommentService commentService, DiaryService diaryService, ApplicationEventPublisher publisher) {
        this.memberService = memberService;
        this.postService = postService;
        this.commentService = commentService;
        this.diaryService = diaryService;
        this.publisher = publisher;
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

    @Scheduled(cron = "0 */30 * * * ?") // 매 30분마다
    public void sendNotifications() {
        List<Diary> diaries = diaryService.findAll();
        LocalTime now = LocalTime.now();

        for (Diary diary : diaries) {
            LocalTime diaryTime = diary.getTime();
            if (now.getHour() == diaryTime.getHour() && now.getMinute() == diaryTime.getMinute()) {
                publisher.publishEvent(new EventBeforeDiaryCheck(this, diary));
            }
        }
    }
}