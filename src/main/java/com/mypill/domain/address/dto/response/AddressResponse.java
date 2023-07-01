package com.mypill.domain.address.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mypill.domain.address.entity.Address;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

@Data
@Builder
public class AddressResponse {

    private String name;
    private String receiverName;
    private String address;
    private String detailAddress;
    private String postCode;
    private String phoneNumber;
    private boolean isDefault;

    public static AddressResponse of(Address address){
        return AddressResponse.builder()
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
