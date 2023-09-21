package com.nineleaps.leaps.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.nineleaps.leaps.dto.AddressDto;
import com.nineleaps.leaps.enums.AddressType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "address")
@Getter
@Setter
@NoArgsConstructor
public class Address {


    // Audit Columns
    @Column(name = "created_at")
    private LocalDateTime addressCreatedAt;

    @Column(name = "updated_at")
    private LocalDateTime addressUpdatedAt;

    @Column(name = "created_by")
    private Long addressCreatedBy;

    @Column(name = "updated_by")
    private Long addressUpdatedBy;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Enumerated(EnumType.STRING)
    @Column(name = "address_type")
    private AddressType addressType;
    @Column(name = "address_line_1")
    private String addressLine1;
    @Column(name = "address_line_2")
    private String addressLine2;
    private String city;
    private String state;
    private String postalCode;
    private String country;
    private boolean defaultAddress;
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private User user;


    public Address(AddressDto addressDto, User user) {
        this.id = addressDto.getId();
        this.addressType = addressDto.getAddressType();
        this.addressLine1 = addressDto.getAddressLine1();
        this.addressLine2 = addressDto.getAddressLine2();
        this.city = addressDto.getCity();
        this.state = addressDto.getState();
        this.postalCode = addressDto.getPostalCode();
        this.country = addressDto.getCountry();
        this.user = user;
        this.defaultAddress = addressDto.isDefaultAddress();
    }


    public void setAuditColumnsCreate(User user) {
        this.addressCreatedAt = user.getCreatedAt();
        this.addressCreatedBy = user.getCreatedBy();
    }

    public void setAuditColumnsUpdate(Long userId){
        this.addressUpdatedAt = LocalDateTime.now();
        this.addressUpdatedBy = userId;
    }
}
