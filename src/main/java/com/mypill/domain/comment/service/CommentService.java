package com.mypill.domain.comment.service;

import com.mypill.domain.comment.dto.request.CommentRequest;
import com.mypill.domain.comment.dto.response.CommentResponse;
import com.mypill.domain.comment.entity.Comment;
import com.mypill.domain.comment.repository.CommentRepository;
import com.mypill.domain.member.entity.Member;
import com.mypill.domain.post.entity.Post;
import com.mypill.domain.post.repository.PostRepository;
import com.mypill.global.rsdata.RsData;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;

    @Transactional
    public RsData<Comment> create(CommentRequest commentRequest, Member member, Long postId) {
        Post post = postRepository.findByIdAndDeleteDateIsNull(postId).orElse(null);
        if (post == null) {
            return RsData.of("F-1", "존재하지 않는 게시물입니다.");
        }
        Comment comment = Comment.createComment(post, member, commentRequest);
        commentRepository.save(comment);
        return RsData.of("S-1", "댓글 등록이 완료되었습니다.", comment);
    }

    @Transactional(readOnly = true)
    public List<CommentResponse> getCommentsWithMembers(Long postId) {
        return commentRepository.findCommentsWithMembers(postId);
    }

    @Transactional
    public RsData<String> update(CommentRequest commentRequest, Member member, Long commentId) {
        Comment comment = commentRepository.findByIdAndDeleteDateIsNull(commentId).orElse(null);
        String newContent = commentRequest.getNewContent();
        if (comment == null) {
            return RsData.of("F-1", "존재하지 않는 댓글입니다.", newContent);

        }
        if (!Objects.equals(comment.getCommenterId(), member.getId())) {
            return RsData.of("F-2", "본인 댓글만 수정할 수 있습니다.", newContent);
        }
        comment.update(newContent);
        return RsData.of("S-1", "댓글이 수정되었습니다.", newContent);
    }

    @Transactional
    public RsData<Comment> softDelete(Member member, Long commentId) {
        Comment comment = commentRepository.findByIdAndDeleteDateIsNull(commentId).orElse(null);
        if (comment == null) {
            return RsData.of("F-1", "존재하지 않는 댓글입니다.");
        }
        if (!Objects.equals(comment.getCommenterId(), member.getId())) {
            return RsData.of("F-2", "본인 댓글만 삭제할 수 있습니다.");
        }
        comment.softDelete();
        return RsData.of("S-1", "댓글 삭제가 완료되었습니다.");
    }

    public List<Comment> getMyComments(Member member) {
        return commentRepository.findByCommenterIdAndDeleteDateIsNullOrderByIdDesc(member.getId());
    }

    @Transactional
    public void hardDelete() {
        commentRepository.hardDelete();
    }

    @Transactional
    public void whenAfterDeleteMember(Member member) {
        List<Comment> comments = commentRepository.findByCommenterId(member.getId());
        for (Comment comment : comments) {
            comment.softDelete();
        }
    }
}