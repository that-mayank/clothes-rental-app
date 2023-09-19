package com.nineleaps.leaps.service.implementation;

import com.nineleaps.leaps.dto.AddressDto;
import com.nineleaps.leaps.model.Address;
import com.nineleaps.leaps.model.User;
import com.nineleaps.leaps.repository.AddressRepository;
import com.nineleaps.leaps.service.AddressServiceInterface;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
@Transactional
public class AddressServiceImpl implements AddressServiceInterface {
    private final AddressRepository addressRepository;

    // Save a new address for the user
    @Override
    public void saveAddress(AddressDto addressDto, User user) {
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
    public List<Address> listAddress(User user) {
        return addressRepository.findAllByUser(user);
    }

    // Read an address by its ID
    @Override
    public Optional<Address> readAddress(Long addressId) {
        return addressRepository.findById(addressId);
    }

    // Read an address by its ID for a specific user
    @Override
    public Address readAddress(User user, Long addressId) {
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
    public void updateAddress(AddressDto addressDto, Long addressId, User user) {
        addressDto.setId(addressId);
        Address address = new Address(addressDto, user);
        addressRepository.save(address);
    }

    // Delete an address by its ID
    @Override
    public void deleteAddress(Long addressId) {
        addressRepository.deleteById(addressId);
    }
}
