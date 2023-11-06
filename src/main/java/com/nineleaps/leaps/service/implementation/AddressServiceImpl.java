package com.nineleaps.leaps.service.implementation;

import com.nineleaps.leaps.dto.AddressDto;
import com.nineleaps.leaps.exceptions.AddressOwnershipException;
import com.nineleaps.leaps.model.Address;
import com.nineleaps.leaps.model.User;
import com.nineleaps.leaps.repository.AddressRepository;
import com.nineleaps.leaps.service.AddressServiceInterface;
import com.nineleaps.leaps.utils.Helper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
@Transactional
public class AddressServiceImpl implements AddressServiceInterface {
    private final AddressRepository addressRepository;
    private final Helper helper;

    // Save a new address for the user
    @Override
    public void saveAddress(AddressDto addressDto, HttpServletRequest request) {
        User user = helper.getUser(request);
        if (addressDto.isDefaultAddress()) {
            // If the new address is set as the default, remove the default flag from other addresses
            List<Address> addresses = addressRepository.findAllByUser(user);
            for (Address addressItr : addresses) {
                addressItr.setDefaultAddress(false);
            }
        }
        // Create a new Address entity and save it
        Address newAddress = new Address(addressDto, user);
        addressRepository.save(newAddress);
    }

    // List all addresses for the user
    @Override
    public List<Address> listAddress(HttpServletRequest request) {
        // JWT : Extracting user info from token
        User user = helper.getUser(request);
        return addressRepository.findAllByUser(user);
    }

    // Read an address by its ID
    @Override
    public Optional<Address> readAddress(Long addressId) {
        return addressRepository.findById(addressId);
    }

    // Read an address by its ID for a specific user
    @Override
    public Address readAddress(HttpServletRequest request, Long addressId) {
        // JWT : Extracting user info from token
        User user = helper.getUser(request);
        List<Address> body = addressRepository.findAllByUser(user);
        for (Address address : body) {
            if (addressId.equals(address.getId())) {
                return address;
            }
        }
        return null;
    }

    // Update an existing address
    @Override
    public void updateAddress(AddressDto addressDto, Long addressId, HttpServletRequest request) {
        Address checkAddress = readAddress(request, addressId);
        if (checkAddress == null)
            throw new AddressOwnershipException("Address does not belong to current user");

        addressDto.setId(addressId);
        Address address = new Address(addressDto, helper.getUser(request));
        addressRepository.save(address);
    }

    // Delete an address by its ID
    @Override
    public void deleteAddress(HttpServletRequest request, Long addressId) {
        // Guard Statement : Check if address belong to given user
        Address checkAddress = readAddress(request, addressId);
        if (checkAddress == null) {
            throw new AddressOwnershipException("Address do not belong to the current user");
        }
        addressRepository.deleteById(addressId);
    }
}
