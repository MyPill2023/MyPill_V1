package com.mypill.domain.comment.repository;

import com.mypill.domain.comment.dto.response.CommentResponse;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

import static com.mypill.domain.member.entity.QMember.member;
import static com.mypill.domain.comment.entity.QComment.comment;

@RequiredArgsConstructor
public class CommentRepositoryImpl implements CommentRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<CommentResponse> findCommentsWithMembers(Long postId) {
        BooleanExpression condition = comment.deleteDate.isNull()
                .and(comment.post.id.eq(postId));

        List<Tuple> tuples = jpaQueryFactory
                .select(comment, member)
                .from(comment)
                .leftJoin(member).on(comment.commenterId.eq(member.id))
                .where(condition)
                .fetch();
        return tuples.stream()
                .map(tuple -> CommentResponse.of(tuple.get(comment), tuple.get(member)))
                .collect(Collectors.toList());
    }
}
