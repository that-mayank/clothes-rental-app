package com.nineleaps.leaps.service;

import com.nineleaps.leaps.model.Address;
import com.nineleaps.leaps.model.User;

import java.util.List;
import java.util.Optional;

public interface AddressServiceInterface {
    void addAddress(Address address, User user);

    List<Address> listAddress(User user);

    Optional<Address> readAddress(Long addressId);

    Address readAddress(User user, Long addressId);

    void updateAddress(Address address, Long addressId, User user);

    void deleteAddress(Long addressId);
}
