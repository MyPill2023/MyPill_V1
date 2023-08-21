package com.mypill.domain.productlike.repository;

import com.mypill.domain.productlike.entity.ProductLike;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductLikeRepository extends JpaRepository<ProductLike, Long> {
    Optional<ProductLike> findByProductId(Long productId);

    ProductLike findByMemberIdAndProductId(Long memberId, Long productId);
}
