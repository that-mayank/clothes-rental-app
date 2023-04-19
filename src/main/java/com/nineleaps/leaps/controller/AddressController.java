package com.nineleaps.leaps.controller;

import com.nineleaps.leaps.common.ApiResponse;
import com.nineleaps.leaps.exceptions.AuthenticationFailException;
import com.nineleaps.leaps.model.Address;
import com.nineleaps.leaps.model.User;
import com.nineleaps.leaps.service.AddressServiceInterface;
import com.nineleaps.leaps.service.AuthenticationServiceInterface;
import com.nineleaps.leaps.utils.Helper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/address")
public class AddressController {
    private final AddressServiceInterface addressService;
    private final AuthenticationServiceInterface authenticationService;

    @Autowired
    public AddressController(AddressServiceInterface addressService, AuthenticationServiceInterface authenticationService) {
        this.addressService = addressService;
        this.authenticationService = authenticationService;
    }

    //add
    @PostMapping("/add")
    public ResponseEntity<ApiResponse> addAddress(@RequestBody @Valid Address address, @RequestParam("token") String token) throws AuthenticationFailException {
        //authenticate token
        authenticationService.authenticate(token);
        //find user
        User user = authenticationService.getUser(token);
        //Add address
        addressService.addAddress(address, user);
        return new ResponseEntity<>(new ApiResponse(true, "Address added successfully"), HttpStatus.CREATED);

    }

    //update
    @PutMapping("/update/{addressId}")
    public ResponseEntity<ApiResponse> updateAddress(@PathVariable("addressId") Long addressId, @RequestBody @Valid Address address, @RequestParam("token") String token) throws AuthenticationFailException {
        //authenticate token
        authenticationService.authenticate(token);
        //find user
        User user = authenticationService.getUser(token);
        //check if addressId is valid or not
        Optional<Address> optionalAddress = addressService.readAddress(addressId);
        if (!optionalAddress.isPresent()) {
            return new ResponseEntity<>(new ApiResponse(false, "Address not valid"), HttpStatus.NOT_FOUND);
        }
        //check if that addressId belong to given user
        Address checkAddress = addressService.readAddress(user, addressId);
        if (!Helper.notNull(checkAddress)) {
            return new ResponseEntity<>(new ApiResponse(false, "Address does not belong to current user"), HttpStatus.FORBIDDEN);
        }
        //update address
        addressService.updateAddress(address, addressId, user);
        return new ResponseEntity<>(new ApiResponse(true, "Address updated successfully"), HttpStatus.OK);
    }

    //listByUserId
    @GetMapping("/{token}")
    public ResponseEntity<List<Address>> listAddress(@PathVariable("token") String token) throws AuthenticationFailException {
        //authenticate token
        authenticationService.authenticate(token);
        //find user
        User user = authenticationService.getUser(token);
        //list address
        List<Address> body = addressService.listAddress(user);
        return new ResponseEntity<>(body, HttpStatus.OK);
    }

    //delete
    @DeleteMapping("/delete/{addressId}")
    public ResponseEntity<ApiResponse> deleteAddress(@PathVariable("addressId") Long addressId, @RequestParam("token") String token) throws AuthenticationFailException {
        //authenticate token
        authenticationService.authenticate(token);
        //find user
        User user = authenticationService.getUser(token);
        //check if addressId is valid or not
        Optional<Address> optionalAddress = addressService.readAddress(addressId);
        if (!optionalAddress.isPresent()) {
            return new ResponseEntity<>(new ApiResponse(false, "Address is invalid"), HttpStatus.NOT_FOUND);
        }
        //check if the address belongs to the current user
        Address checkAddress = addressService.readAddress(user, addressId);
        if (!Helper.notNull(checkAddress)) {
            return new ResponseEntity<>(new ApiResponse(false, "Address do not belong to the current user"), HttpStatus.FORBIDDEN);
        }
        //delete address
        addressService.deleteAddress(addressId, user);
        return new ResponseEntity<>(new ApiResponse(true, "Address deleted successfully"), HttpStatus.OK);
    }
}
