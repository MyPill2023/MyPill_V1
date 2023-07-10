package com.mypill.domain.address.dto.response;

import com.mypill.domain.address.entity.Address;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AddressResponse {

    private Long id;
    private String name;
    private String receiverName;
    private String address;
    private String detailAddress;
    private String postCode;
    private String phoneNumber;
    private boolean isDefault;

    public static AddressResponse of(Address address){
        return AddressResponse.builder()
                .id(address.getId())
                .name(address.getName())
                .receiverName(address.getReceiverName())
                .address(address.getAddress())
                .detailAddress(address.getDetailAddress())
                .postCode(address.getPostCode())
                .phoneNumber(address.getPhoneNumber())
                .isDefault(address.isDefault())
                .build();
    }
}
