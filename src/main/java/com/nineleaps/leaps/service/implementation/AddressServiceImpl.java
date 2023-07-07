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

    @Override
    public void saveAddress(AddressDto addressDto, User user) {
        if (addressDto.isDefaultAddress()) {
            List<Address> addresses = addressRepository.findAllByUser(user);
            for (Address addressItr : addresses) {
                addressItr.setDefaultAddress(false);
            }
        }
        Address newAddress = new Address(addressDto, user);
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
    public void updateAddress(AddressDto addressDto, Long addressId, User user) {
        addressDto.setId(addressId);
        Address address = new Address(addressDto, user);
        addressRepository.save(address);
    }

    @Override
    public void deleteAddress(Long addressId) {
        addressRepository.deleteById(addressId);
    }
}
