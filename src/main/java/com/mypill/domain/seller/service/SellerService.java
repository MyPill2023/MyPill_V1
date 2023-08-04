package com.mypill.domain.seller.service;

import com.mypill.domain.member.entity.Member;
import com.mypill.domain.member.repository.MemberRepository;
import com.mypill.global.rsdata.RsData;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SellerService {
    private final CertificationService certificationService;
    private final MemberRepository memberRepository;

    @Transactional
    public RsData<Member> businessNumberCheck(String businessNumber, Member member) {
        if (memberRepository.findByBusinessNumber(businessNumber).isPresent()) {
            return RsData.of("F-1", "이미 등록된 번호입니다.");
        }
        if (!certificationService.businessNumberCertificated(businessNumber)) {
            return RsData.of("F-2", "인증에 실패했습니다.");
        }
        member.updateBusinessNumber(businessNumber);
        return RsData.of("S-1", "인증에 성공했습니다.", member);
    }

    @Transactional
    public RsData<Member> nutrientBusinessNumberCheck(String nutrientBusinessNumber, Member member) {
        if (memberRepository.findByNutrientBusinessNumber(nutrientBusinessNumber).isPresent()) {
            return RsData.of("F-1", "이미 등록된 번호입니다.");
        }
        if (!certificationService.nutrientBusinessNumberCertificated(nutrientBusinessNumber)) {
            return RsData.of("F-2", "인증에 실패했습니다.");
        }
        member.updateNutrientBusinessNumber(nutrientBusinessNumber);
        return RsData.of("S-1", "인증에 성공했습니다.", member);
    }
}
