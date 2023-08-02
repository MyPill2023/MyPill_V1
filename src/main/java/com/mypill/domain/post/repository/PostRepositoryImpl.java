package com.mypill.domain.post.repository;

import com.mypill.domain.post.dto.response.PostResponse;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static com.mypill.domain.member.entity.QMember.member;
import static com.mypill.domain.post.entity.QPost.post;


@RequiredArgsConstructor
public class PostRepositoryImpl implements PostRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Page<PostResponse> findPostsWithMembers(Pageable pageable) {
        BooleanExpression condition = post.deleteDate.isNull();
        return getPostPagingResponses(pageable, condition);
    }

    public Page<PostResponse> findPostsWithMembersAndTitleContaining(String keyword, Pageable pageable) {
        BooleanExpression condition = post.deleteDate.isNull()
                .and(post.title.likeIgnoreCase("%" + keyword + "%"));
        return getPostPagingResponses(pageable, condition);
    }

    public Page<PostResponse> findPostsWithMembersAndContentContaining(String keyword, Pageable pageable) {
        BooleanExpression condition = post.deleteDate.isNull()
                .and(post.content.likeIgnoreCase("%" + keyword + "%"));
        return getPostPagingResponses(pageable, condition);
    }

    private List<Tuple> getTuples(BooleanExpression condition, Pageable pageable) {
        return jpaQueryFactory
                .select(post, member)
                .from(post)
                .leftJoin(member).on(post.posterId.eq(member.id))
                .where(condition)
                .orderBy(post.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
    }

    private Page<PostResponse> getPostPagingResponses(Pageable pageable, BooleanExpression condition) {
        List<Tuple> tuples = getTuples(condition, pageable);
        List<PostResponse> postResponse = tuples.stream()
                .map(tuple -> PostResponse.of(tuple.get(post), tuple.get(member)))
                .toList();
        long total = jpaQueryFactory
                .selectFrom(post)
                .where(condition)
                .fetch().size();
        return new PageImpl<>(postResponse, pageable, total);
    }
}
