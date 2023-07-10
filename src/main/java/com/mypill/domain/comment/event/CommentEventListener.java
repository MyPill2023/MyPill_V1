package com.mypill.domain.comment.event;

import com.mypill.domain.comment.service.CommentService;
import com.mypill.global.event.EventAfterDeleteMember;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
@RequiredArgsConstructor
public class CommentEventListener {
    private final CommentService commentService;

    @EventListener
    public void listen(EventAfterDeleteMember event) {
        commentService.whenAfterDeleteMember(event.getMember());
    }

}
