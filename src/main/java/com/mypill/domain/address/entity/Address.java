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
    private String name;
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

    private Boolean isDefault;

    public static Address of(Member member, AddressRequest addressRequest){
        return Address.builder()
                .member(member)
                .name(addressRequest.getName())
                .receiverName(addressRequest.getReceiverName())
                .address(addressRequest.getAddress())
                .detailAddress(addressRequest.getDetailAddress())
                .postCode(addressRequest.getPostCode())
                .phoneNumber(addressRequest.getPhoneNumber())
                .isDefault(addressRequest.getIsDefault())
                .build();
    }

    public void updateAddress(AddressRequest addressRequest){
        this.name = addressRequest.getName();
        this.receiverName = addressRequest.getReceiverName();
        this.address = addressRequest.getAddress();
        this.detailAddress = addressRequest.getDetailAddress();
        this.postCode = addressRequest.getPostCode();
        this.phoneNumber = addressRequest.getPhoneNumber();
    }

    public void changeDefaultTrue(){
        this.isDefault = true;
    }

    public void changeDefaultFalse(){
        this.isDefault = false;
    }

}
