package com.mypill.global.scheduler;

import com.mypill.domain.comment.service.CommentService;
import com.mypill.domain.diary.entity.Diary;
import com.mypill.domain.diary.service.DiaryService;
import com.mypill.domain.image.service.ImageService;
import com.mypill.domain.member.service.MemberService;
import com.mypill.domain.notification.service.NotificationService;
import com.mypill.domain.order.service.OrderService;
import com.mypill.domain.post.entity.Post;
import com.mypill.domain.post.service.PostService;
import com.mypill.global.event.EventBeforeDiaryCheck;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class Scheduler {
    private final MemberService memberService;
    private final PostService postService;
    private final CommentService commentService;
    private final DiaryService diaryService;
    private final NotificationService notificationService;
    private final ImageService imageService;
    private final OrderService orderService;
    private final ApplicationEventPublisher publisher;


    @Scheduled(cron = "0 */30 * * * ?") // 매 30분마다
    @SchedulerLock(
            name = "scheduler_lock",
            lockAtLeastFor = "PT29M",
            lockAtMostFor = "PT29M"
    )
    public void sendNotifications() {
        LocalTime now = LocalTime.now();
        LocalTime startTime = now.truncatedTo(ChronoUnit.MINUTES);
        List<Diary> diaries = diaryService.findByTime(startTime);

        for (Diary diary : diaries) {
            publisher.publishEvent(new EventBeforeDiaryCheck(this, diary));
        }
    }

    @Scheduled(cron = "0 0 4 * * ?")
    @SchedulerLock(
            name = "scheduler_lock",
            lockAtLeastFor = "PT59S",
            lockAtMostFor = "PT59S"
    )
    public void hardDeleteUnverifiedMembers() {
        memberService.hardDeleteUnverifiedMembers();
    }

    @Scheduled(cron = "0 1 4 1 * ?") // 매월 1일 04시 01분
    @SchedulerLock(
            name = "scheduler_lock",
            lockAtLeastFor = "PT59S",
            lockAtMostFor = "PT59S"
    )
    public void deleteComments() {
        commentService.hardDelete();
    }

    @Scheduled(cron = "0 2 4 1 * ?")
    @SchedulerLock(
            name = "scheduler_lock",
            lockAtLeastFor = "PT59S",
            lockAtMostFor = "PT59S"
    )
    public void hardDeletePosts() {
        List<Post> deletedPosts = postService.getDeletedPosts();
        for (Post post : deletedPosts) {
            imageService.deleteImageFromServer(post);
        }
        postService.hardDelete();
    }

    @Scheduled(cron = "0 0 0 * * ?")
    @SchedulerLock(
            name = "scheduler_lock",
            lockAtLeastFor = "PT59S",
            lockAtMostFor = "PT59S"
    )
    public void hardDeleteNotifications() {
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(14);
        notificationService.hardDelete(cutoffDate);
    }

    @Scheduled(cron = "0 3 4 * * ?")
    @SchedulerLock(
            name = "scheduler_lock",
            lockAtLeastFor = "PT59S",
            lockAtMostFor = "PT59S"
    )
    public void hardDeleteOrders() {
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(1);
        orderService.hardDelete(cutoffDate);
    }

    @Scheduled(cron = "*/30 * * * * ?")
    public void testTest() {
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(1);
        orderService.hardDelete(cutoffDate);
    }
}