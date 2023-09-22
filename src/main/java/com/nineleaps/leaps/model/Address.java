package com.nineleaps.leaps.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.nineleaps.leaps.dto.AddressDto;
import com.nineleaps.leaps.enums.AddressType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

/**
 * Represents an address associated with a user.
 */

@Entity
@Table(name = "address")
@Getter
@Setter
@NoArgsConstructor
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Type of address (e.g., Home, Office)
    @Enumerated(EnumType.STRING)
    @Column(name = "address_type")
    private AddressType addressType;

    // First line of the address
    @Column(name = "address_line_1")
    private String addressLine1;

    // Second line of the address
    @Column(name = "address_line_2")
    private String addressLine2;

    // City
    private String city;

    // State
    private String state;

    // Postal code
    private String postalCode;

    // Country
    private String country;

    // Indicates if this is the default address for the user
    private boolean defaultAddress;

    // The user to whom this address belongs
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private User user;

    /**
     * Creates an Address object by copying values from another address and associating it with a user.
     * @param address The source address to copy values from.
     * @param user The user to whom this address belongs.
     */
    public Address(Address address, User user) {
        // Copy values from the source address
        this.addressType = address.getAddressType();
        this.addressLine1 = address.getAddressLine1();
        this.addressLine2 = address.getAddressLine2();
        this.city = address.getCity();
        this.state = address.getState();
        this.postalCode = address.getPostalCode();
        this.country = address.getCountry();
        this.defaultAddress = address.isDefaultAddress();

        // Associate with the user
        this.user = user;
    }

    /**
     * Creates an Address object from an AddressDto and associates it with a user.
     * @param addressDto The AddressDto containing address information.
     * @param user The user to whom this address belongs.
     */
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

    /**
     * Creates an Address object with the given address ID and associates it with a user.
     * @param addressId The ID of the address.
     * @param user The user to whom this address belongs.
     */
    public Address(Long addressId, User user) {
        this.id = addressId;
        this.user = user;
    }
}
