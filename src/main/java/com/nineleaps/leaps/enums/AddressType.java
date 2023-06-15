package com.nineleaps.leaps.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AddressType {
    HOME("Home Address"),
    OFFICE("Office Address");

    private String label;

}
