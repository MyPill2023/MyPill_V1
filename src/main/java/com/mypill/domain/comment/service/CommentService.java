package com.mypill.domain.comment.service;

import com.mypill.domain.comment.dto.CommentRequest;
import com.mypill.domain.comment.entity.Comment;
import com.mypill.domain.comment.repository.CommentRepository;
import com.mypill.domain.member.entity.Member;
import com.mypill.domain.post.entity.Post;
import com.mypill.domain.post.repository.PostRepository;
import com.mypill.global.rsData.RsData;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;

    @Transactional
    public RsData<Comment> create(CommentRequest commentRequest, Member member, Long postId) {
        if (member == null) {
            return RsData.of("F-1", "존재하지 않는 회원입니다.");
        }
        Post post = postRepository.findById(postId).orElse(null);
        if (post == null) {
            return RsData.of("F-2", "존재하지 않는 게시물입니다.");
        }
        Comment comment = Comment.builder()
                .post(post)
                .name(member.getName())
                .content(commentRequest.getNewComment())
                .build();
        commentRepository.save(comment);
        return RsData.of("S-1", "댓글 등록이 완료되었습니다.", comment);
    }
}
