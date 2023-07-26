package com.mypill.domain.address.service;

import com.mypill.domain.address.dto.request.AddressRequest;
import com.mypill.domain.address.entity.Address;
import com.mypill.domain.address.repository.AddressRepository;
import com.mypill.domain.member.entity.Member;
import com.mypill.domain.member.service.MemberService;
import com.mypill.global.AppConfig;
import com.mypill.global.rsdata.RsData;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AddressService {
    private final MemberService memberService;
    private final AddressRepository addressRepository;

    @Transactional
    public RsData<Address> create(AddressRequest addressRequest) {
        Member member = memberService.findById(addressRequest.getMemberId()).orElse(null);
        if (member == null) {
            return RsData.of("F-1", "로그인 후 이용 가능합니다.");
        }
        if (isFull(member.getId())) {
            return RsData.of("F-2", "배송지는 최대 " + AppConfig.getMaxAddressCount() + "개 까지 등록 가능합니다.");
        }
        Address address = Address.of(member, addressRequest);
        setDefaultNameIfEmpty(address, addressRequest);
        changeDefaultStatus(address, addressRequest);
        addressRepository.save(address);
        return RsData.of("S-1", "배송지가 추가되었습니다", address);
    }

    public RsData<Address> get(Member actor, Long addressId) {
        Address address = findById(addressId).orElse(null);
        if (address == null) {
            return RsData.of("F-1", "존재하지 않는 배송지입니다.");
        }
        if (address.getDeleteDate() != null) {
            return RsData.of("F-2", "삭제된 배송지입니다.");
        }
        if (!address.getMember().getId().equals(actor.getId())) {
            return RsData.of("F-3", "수정 및 삭제 권한이 없습니다");
        }
        return RsData.of("S-1", "접근 가능한 배송지입니다.", address);
    }

    @Transactional
    public RsData<Address> update(Member actor, Long addressId, AddressRequest addressRequest) {
        RsData<Address> rsData = get(actor, addressId);
        if(rsData.isFail()){
            return RsData.of(rsData.getResultCode(), rsData.getMsg());
        }
        Address address = rsData.getData();
        address.updateAddress(addressRequest);
        setDefaultNameIfEmpty(address, addressRequest);
        changeDefaultStatus(address, addressRequest);
        return RsData.of("S-1", "배송지 수정이 완료되었습니다.", address);
    }

    @Transactional
    public RsData<Address> softDelete(Member actor, Long addressId) {
        RsData<Address> rsData = get(actor, addressId);
        if(rsData.isFail()){
            return RsData.of(rsData.getResultCode(), rsData.getMsg());
        }
        Address address = rsData.getData();
        address.softDelete();
        return RsData.of("S-1", "배송지가 삭제되었습니다.", address);
    }

    public List<Address> findByMemberId(Long memberId) {
        return addressRepository.findByMemberId(memberId);
    }

    public Optional<Address> findById(Long addressId) {
        return addressRepository.findById(addressId);
    }

    public boolean isFull(Long memberId) {
        return countAddressesByMemberId(memberId) >= AppConfig.getMaxAddressCount();
    }

    private int countAddressesByMemberId(Long memberId) {
        return addressRepository.countByMemberIdAndDeleteDateIsNull(memberId);
    }

    private void setDefaultNameIfEmpty(Address address, AddressRequest addressRequest) {
        if (addressRequest.getName().trim().isEmpty()) {
            address.setDefaultName();
        }
    }

    private void changeDefaultStatus(Address address, AddressRequest addressRequest) {
        if (addressRequest.isDefault()) {
            List<Address> myAddresses = findByMemberId(addressRequest.getMemberId());
            myAddresses.stream()
                    .filter(myAddress -> myAddress.getDeleteDate() == null)
                    .forEach(Address::changeDefaultFalse);
            address.changeDefaultTrue();
        } else {
            address.changeDefaultFalse();
        }
    }
}