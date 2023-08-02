package com.mypill.global.scheduler;

import com.mypill.domain.comment.entity.Comment;
import com.mypill.domain.comment.service.CommentService;
import com.mypill.domain.diary.entity.Diary;
import com.mypill.domain.diary.service.DiaryService;
import com.mypill.domain.member.entity.Member;
import com.mypill.domain.member.service.MemberService;
import com.mypill.domain.post.entity.Post;
import com.mypill.domain.post.service.PostService;
import com.mypill.global.event.EventBeforeDiaryCheck;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Component
@Slf4j
public class Scheduler {
    private final MemberService memberService;
    private final PostService postService;
    private final CommentService commentService;
    private final DiaryService diaryService;
    private final ApplicationEventPublisher publisher;

    public Scheduler(MemberService memberService, PostService postService, CommentService commentService, DiaryService diaryService, ApplicationEventPublisher publisher) {
        this.memberService = memberService;
        this.postService = postService;
        this.commentService = commentService;
        this.diaryService = diaryService;
        this.publisher = publisher;
    }

    @Scheduled(cron = "0 1 4 * * *")
    @SchedulerLock(
            name = "scheduler_lock",
            lockAtLeastFor = "PT59S",
            lockAtMostFor = "PT59S"
    )
    public void deleteUnverifiedMembers() {
        List<Member> unverifiedMembers = memberService.getUnverifiedMember();
        for (Member member : unverifiedMembers) {
            memberService.deleteMember(member);
        }
    }

    @Scheduled(cron = "0 2 4 * * *")
    @SchedulerLock(
            name = "scheduler_lock",
            lockAtLeastFor = "PT59S",
            lockAtMostFor = "PT59S"
    )
    public void deletePosts() {
        List<Post> deletedPosts = postService.getDeletedPosts();
        for (Post post : deletedPosts) {
            postService.hardDelete(post);
        }
    }

    @Scheduled(cron = "0 1 4 * * *")
    @SchedulerLock(
            name = "scheduler_lock",
            lockAtLeastFor = "PT59S",
            lockAtMostFor = "PT59S"
    )
    public void deleteComments() {
        List<Comment> deletedComments = commentService.getDeletedComments();
        for (Comment comment : deletedComments) {
            commentService.hardDelete(comment);
        }
    }

    @Scheduled(cron = "0 */30 * * * ?") // 매 30분마다
    @SchedulerLock(
            name = "scheduler_lock",
            lockAtLeastFor = "PT29M",
            lockAtMostFor = "PT29M"
    )
    public void sendNotifications() {
        LocalTime now = LocalTime.now();
        LocalTime startTime = now.truncatedTo(ChronoUnit.MINUTES);
        List<Diary> diaries = diaryService.findByDeleteDateNullAndTime(startTime);

        for (Diary diary : diaries) {
            publisher.publishEvent(new EventBeforeDiaryCheck(this, diary));
        }
    }

}