package com.nineleaps.leaps.dto;

import com.nineleaps.leaps.enums.AddressType;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Getter
@Setter
public class AddressDto {
    private Long id;

    @NotNull(message = "Address type is required")
    private AddressType addressType;

    @NotBlank(message = "Address Line 1 is required")
    private String addressLine1;

    // You may want to use @NotBlank or @Size(max = <maxLength>) for other address fields as needed
    private String addressLine2;

    @NotBlank(message = "City is required")
    private String city;

    @NotBlank(message = "State is required")
    private String state;

    @NotBlank(message = "Postal code is required")
    @Pattern(regexp = "\\d{6}", message = "Postal code must be 6 digits")
    private String postalCode;

    @NotBlank(message = "Country is required")
    private String country;

    @AssertTrue(message = "Default address must be a valid boolean value")
    private boolean defaultAddress;
}
