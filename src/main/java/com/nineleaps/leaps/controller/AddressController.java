package com.nineleaps.leaps.controller;

import com.nineleaps.leaps.common.ApiResponse;
import com.nineleaps.leaps.dto.AddressDto;
import com.nineleaps.leaps.exceptions.AuthenticationFailException;
import com.nineleaps.leaps.model.Address;
import com.nineleaps.leaps.model.User;
import com.nineleaps.leaps.service.AddressServiceInterface;
import com.nineleaps.leaps.utils.Helper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@RestController
@RequestMapping("/api/v1/address")
@AllArgsConstructor
@Api(tags = "Address Api", description = "Contains api for add, update, list and delete address")
@SuppressWarnings("deprecation")
public class AddressController {
    private final AddressServiceInterface addressService;
    private final Helper helper;

    //add
    @ApiOperation(value = "Add new address to particular user")
    @PostMapping("/add")
    public ResponseEntity<ApiResponse> addAddress(@RequestBody @Valid AddressDto addressDto, HttpServletRequest request) throws AuthenticationFailException {
        String authorizationHeader = request.getHeader(AUTHORIZATION);
        String token = authorizationHeader.substring(7);
        User user = helper.getUser(token);
        //Add address
        addressService.saveAddress(addressDto, user);
        return new ResponseEntity<>(new ApiResponse(true, "Address added successfully"), HttpStatus.CREATED);

    }

    //update
    @ApiOperation(value = "update address for particular user")
    @PutMapping("/update/{addressId}")
    public ResponseEntity<ApiResponse> updateAddress(@PathVariable("addressId") Long addressId, @RequestBody @Valid AddressDto addressDto, HttpServletRequest request) throws AuthenticationFailException {
        String authorizationHeader = request.getHeader(AUTHORIZATION);
        String token = authorizationHeader.substring(7);
        User user = helper.getUser(token);

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
        addressService.updateAddress(addressDto, addressId, user);
        return new ResponseEntity<>(new ApiResponse(true, "Address updated successfully"), HttpStatus.OK);
    }

    //listByUserId
    @ApiOperation(value = "List all addresses for particular user")
    @GetMapping("/listAddress")
    public ResponseEntity<List<Address>> listAddress(HttpServletRequest request) throws AuthenticationFailException {

        String authorizationHeader = request.getHeader(AUTHORIZATION);
        String token = authorizationHeader.substring(7);
        User user = helper.getUser(token);

        List<Address> body = addressService.listAddress(user);
        return new ResponseEntity<>(body, HttpStatus.OK);
    }

    //delete
    @ApiOperation(value = "Delete address for particular user")
    @DeleteMapping("/delete/{addressId}")
    public ResponseEntity<ApiResponse> deleteAddress(@PathVariable("addressId") Long addressId, HttpServletRequest request) throws AuthenticationFailException {

        //authenticate token
        String authorizationHeader = request.getHeader(AUTHORIZATION);
        String token = authorizationHeader.substring(7);
        User user = helper.getUser(token);
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
        addressService.deleteAddress(addressId);
        return new ResponseEntity<>(new ApiResponse(true, "Address deleted successfully"), HttpStatus.OK);
    }

    //prefilled address on update address tab
    @GetMapping("/getAddressById/{addressId}")
    public ResponseEntity<Address> getAddressById(@PathVariable("addressId") Long addressId) {
        Optional<Address> optionalAddress = addressService.readAddress(addressId);
        if (optionalAddress.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        Address address = optionalAddress.get();
        return new ResponseEntity<>(address, HttpStatus.OK);
    }
}

