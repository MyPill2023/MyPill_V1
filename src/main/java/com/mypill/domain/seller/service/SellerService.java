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
        boolean brnoCertificated = certificationService.brnoCertificated(brno);
        if (!brnoCertificated) {
            return RsData.of("F-1", "인증에 실패했습니다.");
        }
        member = member.toBuilder()
                .brnoCertificated(true)
                .build();
        member = updateUserType(member);

        memberRepository.save(member);
        return RsData.of("S-1", "인증에 성공했습니다.");
    }

    public RsData certificateNBRNO(String nBrno, Member member) {
        boolean nBrnoCertificated = certificationService.nBrnoCertificated(nBrno);
        if (!nBrnoCertificated) {
            return RsData.of("F-1", "인증에 실패했습니다.");
        }
        member = member.toBuilder()
                .nBrnoCertificated(true)
                .build();
        member = updateUserType(member);
        memberRepository.save(member);
        return RsData.of("S-1", "인증에 성공했습니다.");
    }

    private Member updateUserType(Member member) {
        if (member.isBrnoCertificated() && member.isNBrnoCertificated()) {
            member = member.toBuilder()
                    .userType(2)
                    .build();
        }
        return member;
    }
}
