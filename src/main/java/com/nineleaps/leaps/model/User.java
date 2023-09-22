package com.nineleaps.leaps.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.nineleaps.leaps.dto.user.ProfileUpdateDto;
import com.nineleaps.leaps.enums.Role;
import com.nineleaps.leaps.model.orders.Order;
import com.nineleaps.leaps.model.product.Product;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a user in the application.
 */

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
public class User {

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

    private String profileImageUrl;

    @JsonIgnore
    private String deviceToken;

    // Relationships
    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER)
    private List<Order> orders;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Address> addresses;

    // Lazy maintained here because user mostly lands on borrower flow and here products represents products added in owner flow
    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER)
    private List<Product> products;

    /**
     * Constructor for creating a new user.
     *
     * @param firstName    The first name of the user.
     * @param lastName     The last name of the user.
     * @param email        The email address of the user.
     * @param phoneNumber  The phone number of the user.
     * @param password     The password of the user (hashed or encrypted).
     * @param role         The role of the user (e.g., ROLE_ADMIN, ROLE_USER).
     */
    public User(String firstName, String lastName, String email, String phoneNumber, String password, Role role) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.password = password;
        this.role = role;
        this.products = new ArrayList<>();
    }

    /**
     * Constructor for updating user profile information.
     *
     * @param profileUpdateDto  The DTO containing updated profile information.
     * @param oldUser           The existing user object.
     */
    public User(ProfileUpdateDto profileUpdateDto, User oldUser) {
        this.id = oldUser.getId();
        this.firstName = profileUpdateDto.getFirstName();
        this.lastName = profileUpdateDto.getLastName();
        this.email = profileUpdateDto.getEmail();
        this.phoneNumber = profileUpdateDto.getPhoneNumber();
        this.password = oldUser.getPassword();  // Retain the existing password
        this.role = oldUser.getRole();
        this.profileImageUrl = oldUser.getProfileImageUrl();
        this.deviceToken = oldUser.getDeviceToken();
    }
}
