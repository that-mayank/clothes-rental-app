package com.nineleaps.leaps.dto.user;

import com.nineleaps.leaps.enums.Role;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class SignupDto {
    private @NotBlank String firstName;
    private @NotBlank String lastName;
    private @NotBlank String email;
    private @NotBlank String phoneNumber;
    private @NotBlank String password;
    private @NotBlank Role role;
}
