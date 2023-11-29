package com.nineleaps.leaps.service.implementation;

import com.nineleaps.leaps.dto.AddressDto;
import com.nineleaps.leaps.exceptions.AddressOwnershipException;
import com.nineleaps.leaps.model.Address;
import com.nineleaps.leaps.model.User;
import com.nineleaps.leaps.repository.AddressRepository;
import com.nineleaps.leaps.service.AddressServiceInterface;
import com.nineleaps.leaps.utils.Helper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
@Transactional
@Slf4j // Add SLF4J annotation for logging
public class AddressServiceImpl implements AddressServiceInterface {
    private final AddressRepository addressRepository;
    private final Helper helper;

    @Override
    public void saveAddress(AddressDto addressDto, HttpServletRequest request) {
        User user = helper.getUserFromToken(request);
        log.info("Saving a new address for user: {}", user.getEmail());
        if (addressDto.isDefaultAddress()) {
            log.info("Setting the new address as the default address for user: {}", user.getEmail());
            List<Address> addresses = addressRepository.findAllByUser(user);
            for (Address addressItr : addresses) {
                addressItr.setDefaultAddress(false);
            }
        }
        Address newAddress = new Address(addressDto, user);
        addressRepository.save(newAddress);
        log.info("New address saved successfully for user: {}", user.getEmail());
    }

    @Override
    public List<Address> listAddress(HttpServletRequest request) {
        User user = helper.getUserFromToken(request);
        log.info("Fetching the list of addresses for user: {}", user.getEmail());
        List<Address> addresses = addressRepository.findAllByUser(user);
        log.info("Fetched {} addresses for user: {}", addresses.size(), user.getEmail());
        return addresses;
    }

    @Override
    public Optional<Address> readAddress(Long addressId) {
        log.info("Fetching address by ID: {}", addressId);
        return addressRepository.findById(addressId);
    }

    @Override
    public Address readAddress(HttpServletRequest request, Long addressId) {
        User user = helper.getUserFromToken(request);
        log.info("Fetching address by ID for user: {} with address ID: {}", user.getEmail(), addressId);
        List<Address> addresses = addressRepository.findAllByUser(user);
        for (Address address : addresses) {
            if (addressId.equals(address.getId())) {
                log.info("Fetched address by ID: {} for user: {}", addressId, user.getEmail());
                return address;
            }
        }
        log.warn("Address not found by ID: {} for user: {}", addressId, user.getEmail());
        return null;
    }

    @Override
    public void updateAddress(AddressDto addressDto, Long addressId, HttpServletRequest request) {
        Address checkAddress = readAddress(request, addressId);
        if (checkAddress == null) {
            log.error("Address with ID: {} does not belong to the current user", addressId);
            throw new AddressOwnershipException("Address does not belong to the current user");
        }
        addressDto.setId(addressId);
        Address address = new Address(addressDto, helper.getUserFromToken(request));
        addressRepository.save(address);
        log.info("Address with ID: {} updated for user: {}", addressId, address.getUser().getEmail());
    }

    @Override
    public void deleteAddress(HttpServletRequest request, Long addressId) {
        Address checkAddress = readAddress(request, addressId);
        if (checkAddress == null) {
            log.error("Address with ID: {} does not belong to the current user", addressId);
            throw new AddressOwnershipException("Address does not belong to the current user");
        }
        log.info("Deleting address with ID: {} for user: {}", addressId, checkAddress.getUser().getEmail());
        addressRepository.deleteById(addressId);
    }
}
