package com.nineleaps.leaps.service;

import com.nineleaps.leaps.model.Address;
import com.nineleaps.leaps.model.User;
import com.nineleaps.leaps.repository.AddressRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AddressService implements AddressServiceInterface {
    private final AddressRepository addressRepository;

    @Autowired
    public AddressService(AddressRepository addressRepository) {
        this.addressRepository = addressRepository;
    }

    @Override
    public void addAddress(Address address, User user) {
        Address newAddress = new Address(address, user);
        if (newAddress.isDefaultAddress()) {
            defaultAddress(newAddress, user);
        }
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
        if (address.isDefaultAddress()) {
            defaultAddress(address, user);
        }
        addressRepository.save(address);
    }

    private void defaultAddress(Address address, User user) {
        List<Address> addresses = addressRepository.findAllByUser(user);
        for (Address itr : addresses) {
            if (itr.isDefaultAddress()) {
                itr.setDefaultAddress(false);
            }
            addressRepository.save(itr);
        }
    }

    @Override
    public void deleteAddress(Long addressId, User user) {
        Address address = addressRepository.findById(addressId).get();
        if (address.isDefaultAddress()) {
            List<Address> addresses = addressRepository.findAllByUser(user);
            if (addresses.size() > 1) {
                for (Address itr : addresses) {
                    if (addressId != itr.getId()) {
                        itr.setDefaultAddress(true);
                        addressRepository.deleteById(addressId);
                        return;
                    }
                }
                //if size is less than or equal to one we don't do anything. Just delete the address
            }
        }
        addressRepository.deleteById(addressId);
    }
}
