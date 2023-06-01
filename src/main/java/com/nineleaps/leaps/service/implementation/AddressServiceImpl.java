package com.nineleaps.leaps.service.implementation;

import com.nineleaps.leaps.model.Address;
import com.nineleaps.leaps.model.User;
import com.nineleaps.leaps.repository.AddressRepository;
import com.nineleaps.leaps.service.AddressServiceInterface;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class AddressServiceImpl implements AddressServiceInterface {
    private final AddressRepository addressRepository;

    @Override
    public void addAddress(Address address, User user) {
        if (address.isDefaultAddress()) {
            List<Address> addresses = addressRepository.findAllByUser(user);
            for (Address addressItr : addresses) {
                addressItr.setDefaultAddress(false);
            }
        }
        Address newAddress = new Address(address, user);
        addressRepository.save(newAddress);
    }

    @Override
    public List<Address> listAddress(User user) {
        return addressRepository.findAllByUser(user);
    }

    @Override
    public Optional<Address> readAddress(Long addressId) {
        return addressRepository.findById(addressId);

    }

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

    @Override
    public void updateAddress(Address address, Long addressId, User user) {
        address.setId(addressId);
        address.setUser(user);
        addressRepository.save(address);
    }

    @Override
    public void deleteAddress(Long addressId) {
        addressRepository.deleteById(addressId);
    }
}
