package com.mypill.domain.address.entity;

import com.mypill.domain.address.dto.request.AddressRequest;
import com.mypill.domain.member.entity.Member;
import com.mypill.global.base.entitiy.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotNull;
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
    @NotNull
    private String receiverName;
    @NotNull
    private String address;
    @NotNull
    private String detailAddress;
    @NotNull
    private String postCode;
    @NotNull
    private String phoneNumber;
    private boolean isDefault;

    public static Address of(Member member, AddressRequest addressRequest) {
        return Address.builder()
                .member(member)
                .name(addressRequest.getName())
                .receiverName(addressRequest.getReceiverName())
                .address(addressRequest.getAddress())
                .detailAddress(addressRequest.getDetailAddress())
                .postCode(addressRequest.getPostCode())
                .phoneNumber(addressRequest.getPhoneNumber())
                .isDefault(addressRequest.isDefault())
                .build();
    }

    public void updateAddress(AddressRequest addressRequest) {
        this.name = addressRequest.getName();
        this.receiverName = addressRequest.getReceiverName();
        this.address = addressRequest.getAddress();
        this.detailAddress = addressRequest.getDetailAddress();
        this.postCode = addressRequest.getPostCode();
        this.phoneNumber = addressRequest.getPhoneNumber();
    }

    public void setDefaultName() {
        this.name = this.receiverName + "의 배송지";
    }

    public void changeDefaultTrue() {
        this.isDefault = true;
    }

    public void changeDefaultFalse() {
        this.isDefault = false;
    }
}