package com.nineleaps.leaps.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AddressType {
    BILLING("Billing Address"),
    SHIPPING("Shipping Address");

    private String label;

}
