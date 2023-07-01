package com.mypill.domain.address.service;

import com.mypill.domain.address.dto.request.AddressRequest;
import com.mypill.domain.address.entity.Address;
import com.mypill.domain.address.repository.AddressRepository;
import com.mypill.domain.member.entity.Member;
import com.mypill.domain.member.service.MemberService;
import com.mypill.global.rq.Rq;
import com.mypill.global.rsData.RsData;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AddressService {
    private final MemberService memberService;
    private final AddressRepository addressRepository;
    private final Rq rq;

    public RsData<Address> create(AddressRequest addressRequest){
        Member member = memberService.findById(addressRequest.getMemberId()).orElse(null);
        Address address = Address.of(member, addressRequest);
        addressRepository.save(address);
        return RsData.of("S-1", "배송지가 추가되었습니다", address);
    }

    public List<Address> findByMemberId(Long memberId){
        return addressRepository.findByMemberId(memberId);
    }
}
