package com.nineleaps.leaps.service;

import com.nineleaps.leaps.dto.AddressDto;
import com.nineleaps.leaps.model.Address;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;

public interface AddressServiceInterface {
    void saveAddress(AddressDto addressDto, HttpServletRequest request);

    List<Address> listAddress(HttpServletRequest request);

    Optional<Address> readAddress(Long addressId);

    Address readAddress(HttpServletRequest request, Long addressId);

    void updateAddress(AddressDto addressDto, Long addressId, HttpServletRequest request);

    void deleteAddress(HttpServletRequest request, Long addressId);
}
