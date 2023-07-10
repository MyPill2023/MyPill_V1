package com.mypill.domain.post.event;

import com.mypill.domain.post.service.PostService;
import com.mypill.global.event.EventAfterDeleteMember;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
@RequiredArgsConstructor
public class PostEventListener {
    private final PostService postService;

    @EventListener
    public void listen(EventAfterDeleteMember event) {
        postService.whenAfterDeleteMember(event.getMember());
    }

}
