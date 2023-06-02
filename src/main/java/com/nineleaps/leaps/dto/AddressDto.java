package com.nineleaps.leaps.dto;

import com.nineleaps.leaps.enums.AddressType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddressDto {
    private Long id;
    private AddressType addressType;
    private String addressLine1;
    private String addressLine2;
    private String city;
    private String state;
    private String postalCode;
    private String country;
    private boolean defaultAddress;
}
