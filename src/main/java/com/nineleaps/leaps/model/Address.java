package com.nineleaps.leaps.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.nineleaps.leaps.dto.AddressDto;
import com.nineleaps.leaps.enums.AddressType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "address")
@Getter
@Setter
@NoArgsConstructor
public class Address {
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

    public Address(Address address, User user) {
        this.addressType = address.getAddressType();
        this.addressLine1 = address.getAddressLine1();
        this.addressLine2 = address.getAddressLine2();
        this.city = address.getCity();
        this.state = address.getState();
        this.postalCode = address.getPostalCode();
        this.country = address.getCountry();
        this.user = user;
        this.defaultAddress = address.isDefaultAddress();
    }

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

    public Address(Long addressId, User user) {
        this.id = addressId;
        this.user = user;
    }
}
