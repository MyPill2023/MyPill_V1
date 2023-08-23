package com.mypill.domain.productlike.repository;

import com.mypill.domain.productlike.entity.ProductLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductLikeRepository extends JpaRepository<ProductLike, Long> {
    ProductLike findByMemberIdAndProductId(Long memberId, Long productId);

    @Query("select pl.productId from ProductLike pl where pl.memberId = :memberId")
    List<Long> findProductIdsByMemberId(@Param("memberId") Long memberId);
}
