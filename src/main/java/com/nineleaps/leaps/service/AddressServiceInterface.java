package com.nineleaps.leaps.service;

import com.nineleaps.leaps.model.Address;
import com.nineleaps.leaps.model.User;

import java.util.List;
import java.util.Optional;

public interface AddressServiceInterface {
    public void addAddress(Address address, User user);

    public List<Address> listAddress(User user);

    public Optional<Address> readAddress(Long addressId);

    public Address readAddress(User user, Long addressId);

    public void updateAddress(Address address, Long addressId, User user);

    public void deleteAddress(Long addressId, User user);
}
