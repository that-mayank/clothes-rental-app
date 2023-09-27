package com.nineleaps.leaps.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.nineleaps.leaps.dto.user.ProfileUpdateDto;
import com.nineleaps.leaps.enums.Role;
import com.nineleaps.leaps.model.orders.Order;
import com.nineleaps.leaps.model.product.Product;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
public class User {

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "created_by")
    private Long createdBy;

    @Column(name = "updated_by")
    private Long updatedBy;

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

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<Order> orders;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Address> addresses;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<Product> products;

    @OneToOne(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private UserLoginInfo userLoginInfo;



    public void setAuditColumnsCreate(User user) {
            this.createdAt = user.getCreatedAt();
            this.createdBy = user.getCreatedBy();
    }

    public void setAuditColumnsUpdate(Long userId){
        this.updatedAt = LocalDateTime.now();
        this.updatedBy = userId;
    }


    public User(String firstName, String lastName, String email, String phoneNumber, String password, Role role) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.password = password;
        this.role = role;
        this.products = new ArrayList<>();

    }

    public User(ProfileUpdateDto profileUpdateDto, User oldUser) {
        this.id = oldUser.getId();
        this.firstName = profileUpdateDto.getFirstName();
        this.lastName = profileUpdateDto.getLastName();
        this.email = profileUpdateDto.getEmail();
        this.phoneNumber = profileUpdateDto.getPhoneNumber();
        this.password = oldUser.getPassword();
        this.role = oldUser.getRole();
        this.profileImageUrl = oldUser.getProfileImageUrl();
        this.deviceToken = oldUser.getDeviceToken();
    }
}
