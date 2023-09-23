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
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("/api/v1/address")
@AllArgsConstructor
@Api(tags = "Address Api", description = "Contains api for add, update, list and delete address")
@SuppressWarnings("deprecation")
public class AddressController {

    // Linking layers using constructor injection
    private final AddressServiceInterface addressService;
    private final Helper helper;

    // API: Allows the user to add address
    @ApiOperation(value = "Add new address to particular user")
    @PostMapping(value = "/add",consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('OWNER', 'BORROWER')") // Adding Method Level Authorization Via RBAC-Role-Based Access Control
    public ResponseEntity<ApiResponse> addAddress(@RequestBody @Valid AddressDto addressDto, HttpServletRequest request) throws AuthenticationFailException {

        // Extract User from the token
        User user = helper.getUserFromToken(request);


        // Calling the service layer to Add address
        addressService.saveAddress(addressDto, user);

        // Status Code : 201-HttpStatus.CREATED
        return new ResponseEntity<>(new ApiResponse(true, "Address added successfully"), HttpStatus.CREATED);

    }

    // API - Allows the user to update existing address via addressID
    @ApiOperation(value = "update address for particular user")
    @PutMapping(value = "/update/{addressId}",consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('OWNER', 'BORROWER')") // Adding Method Level Authorization Via RBAC-Role-Based Access Control
    public ResponseEntity<ApiResponse> updateAddress(@PathVariable("addressId") Long addressId, @RequestBody @Valid AddressDto addressDto, HttpServletRequest request) throws AuthenticationFailException {

        // Extract User from the token
        User user = helper.getUserFromToken(request);


        //check if addressId is valid or not
        Optional<Address> optionalAddress = addressService.readAddress(addressId);
        if (optionalAddress.isEmpty()) {

            // Status Code: 404-HttpStatus.NOT_FOUND
            return new ResponseEntity<>(new ApiResponse(false, "Address not valid"), HttpStatus.NOT_FOUND);
        }

        // check if that addressId belong to given user
        Address checkAddress = addressService.readAddress(user, addressId);
        if (!Helper.notNull(checkAddress)) {
            return new ResponseEntity<>(new ApiResponse(false, "Address does not belong to current user"), HttpStatus.FORBIDDEN);
        }
        // Calling the service layer to update address
        addressService.updateAddress(addressDto, addressId, user);

        // Status Code : 200-HttpStatus.OK
        return new ResponseEntity<>(new ApiResponse(true, "Address updated successfully"), HttpStatus.OK);
    }

    // API - Allows the user to get all his address by using his UserId
    @ApiOperation(value = "List all addresses for particular user")
    @GetMapping(value = "/listAddress",produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('OWNER', 'BORROWER')") // Adding Method Level Authorization Via RBAC-Role-Based Access Control
    public ResponseEntity<List<Address>> listAddress(HttpServletRequest request) throws AuthenticationFailException {
        // Extract User from the token
        User user = helper.getUserFromToken(request);


        // Calling the Service Layer to list addresses of a particular user
        List<Address> body = addressService.listAddress(user);

        // Status Code : 200-HttpStatus.OK
        return new ResponseEntity<>(body, HttpStatus.OK);
    }

    //API - Allows the user to delete a particular address
    @ApiOperation(value = "Delete address for particular user")
    @DeleteMapping("/delete/{addressId}")
    @PreAuthorize("hasAnyAuthority('OWNER', 'BORROWER')") // Adding Method Level Authorization Via RBAC-Role-Based Access Control
    public ResponseEntity<ApiResponse> deleteAddress(@PathVariable("addressId") Long addressId, HttpServletRequest request) throws AuthenticationFailException {

        // Extract User from the token
        User user = helper.getUserFromToken(request);


        //check if addressId is valid or not
        Optional<Address> optionalAddress = addressService.readAddress(addressId);
        if (optionalAddress.isEmpty()) {
            return new ResponseEntity<>(new ApiResponse(false, "Address is invalid"), HttpStatus.NOT_FOUND);
        }
        //check if the address belongs to the current user
        Address checkAddress = addressService.readAddress(user, addressId);

        // Check if the address is not null
        if (!Helper.notNull(checkAddress)) {

            // Status Code: 200-HttpStatus.OK
            return new ResponseEntity<>(new ApiResponse(false, "Address not found"), HttpStatus.OK);
        }
        // Calling the service layer to delete address
        addressService.deleteAddress(addressId);

        // Status Code : 200-HttpStatus.OK
        return new ResponseEntity<>(new ApiResponse(true, "Address deleted successfully"), HttpStatus.OK);
    }

    // API - Helps to get prefilled address on update address tab
    @GetMapping(value = "/getAddressById/{addressId}",produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('OWNER', 'BORROWER')") // Adding Method Level Authorization Via RBAC-Role-Based Access Control
    public ResponseEntity<Address> getAddressById(@PathVariable("addressId") Long addressId) {

        // Calling the service layer to get address
        Optional<Address> optionalAddress = addressService.readAddress(addressId);

        // Check if the address is empty
        if (optionalAddress.isEmpty()) {

            // Status Code: 404-HttpStatus.NOT_FOUND
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        Address address = optionalAddress.get();
        // Status Code : 200-HttpStatus.OK
        return new ResponseEntity<>(address, HttpStatus.OK);
    }
}

