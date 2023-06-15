package com.nineleaps.leaps.dto.user;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class ProfileUpdateDto {
    private @NotBlank String firstName;
    private @NotBlank String lastName;
    private @NotBlank String email;
    private @NotBlank String phoneNumber;
}
