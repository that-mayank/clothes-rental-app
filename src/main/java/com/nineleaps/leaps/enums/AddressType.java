package com.nineleaps.leaps.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@AllArgsConstructor
public enum AddressType {
    Home("Home Address"),
    Office("Office Address");

    private String label;

}
