package com.mypill.domain.member.repository;

import com.mypill.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByUsername(String username);

    Optional<Member> findByEmail(String email);

    Optional<Member> findByBusinessNumber(String businessNumber);

    Optional<Member> findByNutrientBusinessNumber(String nutrientBusinessNumber);

    List<Member> findByEmailVerifiedFalse();
}