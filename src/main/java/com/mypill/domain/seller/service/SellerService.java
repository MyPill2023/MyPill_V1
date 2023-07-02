package com.mypill.domain.seller.service;

import com.mypill.domain.member.entity.Member;
import com.mypill.domain.member.repository.MemberRepository;
import com.mypill.global.rsData.RsData;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SellerService {
    private final CertificationService certificationService;
    private final MemberRepository memberRepository;

    public RsData certificateBRNO(String brno, Member member) {
        boolean isQualifiedBrno = certificationService.isQualifiedBusiness(brno);
        if (!isQualifiedBrno) {
            return RsData.of("F-1", "인증에 실패했습니다.");
        }
        member.businessCertificate();
        memberRepository.save(member);
        return RsData.of("S-1", "인증에 성공했습니다.");
    }
}
