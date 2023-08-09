package com.mypill.domain.product.repository;

import com.mypill.domain.product.entity.Product;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static com.mypill.domain.product.entity.QProduct.product;

@RequiredArgsConstructor
public class ProductRepositoryImpl implements ProductRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<Product> findTop5ProductsBySales() {
        return jpaQueryFactory.selectFrom(product)
                .where(
                        product.deleteDate.isNull(),
                        product.stock.gt(0)
                )
                .orderBy(product.sales.desc())
                .limit(5)
                .fetch();
    }

    @Override
    public Page<Product> findAllProduct(Pageable pageable) {
        JPAQuery<Product> query = jpaQueryFactory.selectFrom(product)
                .leftJoin(product.image).fetchJoin()
                .where(
                        product.deleteDate.isNull(),
                        product.stock.gt(0)
                );

        long total = query.fetch().size();
        List<Product> content = query
                .orderBy(product.sales.desc())
                .offset(pageable.getOffset()).limit(pageable.getPageSize())
                .fetch();
        System.out.println("시작끝");

        return new PageImpl<>(content, pageable, total);
    }

    @Override
    public Page<Product> findAllProductByNutrientId(Long nutrientId, Pageable pageable) {
        JPAQuery<Product> query = jpaQueryFactory.selectFrom(product)
                .leftJoin(product.image).fetchJoin()
                .where(
                        product.nutrients.any().id.eq(nutrientId),
                        product.deleteDate.isNull(),
                        product.stock.gt(0)
                );
        long total = query.fetch().size();
        List<Product> content = query
                .orderBy(product.sales.desc())
                .offset(pageable.getOffset()).limit(pageable.getPageSize())
                .fetch();
        return new PageImpl<>(content, pageable, total);
    }

    @Override
    public Page<Product> findAllProductByCategoryId(Long categoryId, Pageable pageable) {
        JPAQuery<Product> query = jpaQueryFactory.selectFrom(product)
                .leftJoin(product.image).fetchJoin()
                .where(
                        product.categories.any().id.eq(categoryId),
                        product.deleteDate.isNull(),
                        product.stock.gt(0)
                );
        long total = query.fetch().size();
        List<Product> content = query
                .orderBy(product.sales.desc())
                .offset(pageable.getOffset()).limit(pageable.getPageSize())
                .fetch();
        return new PageImpl<>(content, pageable, total);
    }

    @Override
    public Page<Product> findAllProductBySellerId(Long sellerId, Pageable pageable) {
        JPAQuery<Product> query = jpaQueryFactory.selectFrom(product)
                .leftJoin(product.image).fetchJoin()
                .where(
                        product.seller.id.eq(sellerId),
                        product.deleteDate.isNull()
                );
        long total = query.fetch().size();
        List<Product> content = query
                .orderBy(product.id.desc())
                .offset(pageable.getOffset()).limit(pageable.getPageSize())
                .fetch();
        return new PageImpl<>(content, pageable, total);
    }
}
