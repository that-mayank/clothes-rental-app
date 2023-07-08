package com.nineleaps.leaps.dto.user;

import com.nineleaps.leaps.enums.Role;
import com.nineleaps.leaps.model.User;
import lombok.Getter;
import lombok.Setter;


import java.util.Objects;

import static com.nineleaps.leaps.LeapsApplication.NGROK;

@Getter
@Setter
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

    @Override
    public String toString() {
        return "UserDto{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", role=" + role +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserDto userDto = (UserDto) o;
        return Objects.equals(id, userDto.id) &&
                Objects.equals(firstName, userDto.firstName) &&
                Objects.equals(lastName, userDto.lastName) &&
                Objects.equals(email, userDto.email) &&
                Objects.equals(phoneNumber, userDto.phoneNumber) &&
                role == userDto.role &&
                Objects.equals(profileImageUrl, userDto.profileImageUrl);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, firstName, lastName, email, phoneNumber, role, profileImageUrl);
    }




}
