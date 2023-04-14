package com.nineleaps.leaps.model;

import com.nineleaps.leaps.enums.Role;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;

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

    public User(String firstName, String lastName, String email, String phoneNumber, String password, Role role) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.password = password;
        this.role = role;
    }
}
