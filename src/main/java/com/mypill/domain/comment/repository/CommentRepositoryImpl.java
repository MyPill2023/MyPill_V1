package com.mypill.domain.comment.repository;

import com.mypill.domain.comment.dto.CommentResponse;
import com.mypill.domain.comment.entity.QComment;
import com.mypill.domain.member.entity.QMember;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;


@RequiredArgsConstructor
public class CommentRepositoryImpl implements CommentRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<CommentResponse> findCommentsWithMembers(Long postId) {
        QComment qComment = QComment.comment;
        QMember qMember = QMember.member;

        BooleanExpression condition = qComment.deleteDate.isNull()
                .and(qComment.post.id.eq(postId));

        List<Tuple> tuples = jpaQueryFactory
                .select(qComment, qMember)
                .from(qComment)
                .leftJoin(qMember).on(qComment.commenterId.eq(qMember.id))
                .where(condition)
                .fetch();
        return tuples.stream()
                .map(tuple -> new CommentResponse(tuple.get(qComment), tuple.get(qMember)))
                .collect(Collectors.toList());
    }
}
