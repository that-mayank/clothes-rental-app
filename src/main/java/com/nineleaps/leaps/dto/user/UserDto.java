package com.nineleaps.leaps.dto.user;

import com.nineleaps.leaps.enums.Role;
import com.nineleaps.leaps.model.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


import java.util.Objects;

import static com.nineleaps.leaps.LeapsApplication.NGROK;

@Getter
@Setter
@NoArgsConstructor
public class UserDto {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private Role role;
    private String profileImageUrl;

    public UserDto(User user) {
        this.id = user.getId();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.email = user.getEmail();
        this.phoneNumber = user.getPhoneNumber();
        this.role = user.getRole();
        if (user.getProfileImageUrl() == null) {
            this.profileImageUrl = null;
        } else {
            this.profileImageUrl = NGROK + user.getProfileImageUrl();
        }
    }





}
