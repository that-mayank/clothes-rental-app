package com.nineleaps.leaps.dto.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@AllArgsConstructor
public class LoginDto {
    private @NotBlank String email;
    private @NotBlank String password;
}
