package com.mypill.domain.product.repository;

import com.mypill.domain.product.entity.Product;
import com.querydsl.core.QueryResults;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static com.mypill.domain.category.entity.QCategory.category;
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
        QueryResults<Product> results = jpaQueryFactory.selectFrom(product)
                .where(
                        product.deleteDate.isNull(),
                        product.stock.gt(0)
                )
                .orderBy(product.sales.desc())
                .offset(pageable.getOffset()).limit(pageable.getPageSize())
                .fetchResults();

        long total = results.getTotal();
        List<Product> content = results.getResults();

        return new PageImpl<>(content, pageable, total);
    }

    @Override
    public Page<Product> findAllProductByNutrientId(Long nutrientId, Pageable pageable) {
        QueryResults<Product> results = jpaQueryFactory.selectFrom(product)
                .where(
                        product.nutrients.any().id.eq(nutrientId),
                        product.deleteDate.isNull(),
                        product.stock.gt(0)
                )
                .orderBy(product.sales.desc())
                .offset(pageable.getOffset()).limit(pageable.getPageSize())
                .fetchResults();

        long total = results.getTotal();
        List<Product> content = results.getResults();

        return new PageImpl<>(content, pageable, total);
    }

    @Override
    public Page<Product> findAllProductByCategoryId(Long categoryId, Pageable pageable) {
        QueryResults<Product> results = jpaQueryFactory.selectFrom(product)
                .leftJoin(product.categories, category).fetchJoin()
                .where(
                        category.id.eq(categoryId),
                        product.deleteDate.isNull(),
                        product.stock.gt(0)
                )
                .orderBy(product.sales.desc())
                .offset(pageable.getOffset()).limit(pageable.getPageSize())
                .fetchResults();

        long total = results.getTotal();
        List<Product> content = results.getResults();

        return new PageImpl<>(content, pageable, total);
    }

}
