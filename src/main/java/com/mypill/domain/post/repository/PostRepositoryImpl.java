package com.mypill.domain.post.repository;

import com.mypill.domain.member.entity.QMember;
import com.mypill.domain.post.dto.PostResponse;
import com.mypill.domain.post.entity.QPost;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.stream.Collectors;


@RequiredArgsConstructor
public class PostRepositoryImpl implements PostRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Page<PostResponse> findPostsWithMembers(Pageable pageable) {
        QPost qPost = QPost.post;
        QMember qMember = QMember.member;
        BooleanExpression condition = qPost.deleteDate.isNull();
        return getPostPagingResponses(pageable, qPost, qMember, condition);
    }

    public Page<PostResponse> findPostsWithMembersAndTitleContaining(String keyword, Pageable pageable) {
        QPost qPost = QPost.post;
        QMember qMember = QMember.member;
        BooleanExpression condition = qPost.deleteDate.isNull()
                .and(qPost.title.likeIgnoreCase("%" + keyword + "%"));
        return getPostPagingResponses(pageable, qPost, qMember, condition);
    }

    public Page<PostResponse> findPostsWithMembersAndContentContaining(String keyword, Pageable pageable) {
        QPost qPost = QPost.post;
        QMember qMember = QMember.member;
        BooleanExpression condition = qPost.deleteDate.isNull()
                .and(qPost.content.likeIgnoreCase("%" + keyword + "%"));
        return getPostPagingResponses(pageable, qPost, qMember, condition);
    }

    private List<Tuple> getTuples(QPost qPost, QMember qMember, BooleanExpression condition, Pageable pageable) {
        return jpaQueryFactory
                .select(qPost, qMember)
                .from(qPost)
                .leftJoin(qMember).on(qPost.posterId.eq(qMember.id))
                .where(condition)
                .orderBy(qPost.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
    }

    private Page<PostResponse> getPostPagingResponses(Pageable pageable, QPost qPost, QMember qMember, BooleanExpression condition) {
        List<Tuple> tuples = getTuples(qPost, qMember, condition, pageable);
        List<PostResponse> postResponse = tuples.stream()
                .map(tuple -> new PostResponse(tuple.get(qPost), tuple.get(qMember)))
                .collect(Collectors.toList());
        long total = jpaQueryFactory
                .selectFrom(qPost)
                .where(condition)
                .fetch().size();
        return new PageImpl<>(postResponse, pageable, total);
    }
}
