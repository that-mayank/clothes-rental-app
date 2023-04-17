package com.nineleaps.leaps.model;

import com.nineleaps.leaps.dto.user.ProfileUpdateDto;
import com.nineleaps.leaps.enums.Role;
import com.nineleaps.leaps.model.orders.Order;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
public class User implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "first_name")
    private String firstName;
    @Column(name = "last_name")
    private String lastName;
    private String email;
    @Column(name = "phone_number")
    private String phoneNumber;
    private String password;
    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private Role role;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<Order> orders;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Address> addresses;

    public User(String firstName, String lastName, String email, String phoneNumber, String password, Role role) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.password = password;
        this.role = role;
    }

    public User(ProfileUpdateDto profileUpdateDto, User oldUser) {
        this.id = oldUser.getId();
        this.firstName = profileUpdateDto.getFirstName();
        this.lastName = profileUpdateDto.getLastName();
        this.email = profileUpdateDto.getEmail();
        this.phoneNumber = profileUpdateDto.getPhoneNumber();
        this.password = oldUser.getPassword();
        this.role = oldUser.getRole();
    }
}
