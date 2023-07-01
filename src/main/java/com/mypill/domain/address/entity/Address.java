package com.mypill.domain.address.entity;

import com.mypill.domain.address.dto.request.AddressRequest;
import com.mypill.domain.member.entity.Member;
import com.mypill.global.base.entitiy.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Address extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;
    @Column(nullable = false)
    private String receiverName;
    @Column(nullable = false)
    private String address;
    @Column(nullable = false)
    private String detailAddress;
    @Column(nullable = false)
    private String postCode;
    @Column(nullable = false)
    private String phoneNumber;

    private boolean isDefault;

    public static Address of(Member member, AddressRequest addressRequest){
        return Address.builder()
                .member(member)
                .receiverName(addressRequest.getReceiverName())
                .address(addressRequest.getAddress())
                .detailAddress(addressRequest.getDetailAddress())
                .postCode(addressRequest.getPostCode())
                .phoneNumber(addressRequest.getPhoneNumber())
                .isDefault(addressRequest.isDefault())
                .build();
    }

}
