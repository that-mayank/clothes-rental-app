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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
@Api(tags = "Address Api", description = "Contains API for add, update, list, and delete address")
@SuppressWarnings("deprecation")

public class AddressController {

   
    private final AddressServiceInterface addressService;
    private final Helper helper;

    private static final Logger log = LoggerFactory.getLogger(AddressController.class);

    @ApiOperation(value = "Add new address to a particular user")
    @PostMapping(value = "/add", consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('OWNER', 'BORROWER')")
    public ResponseEntity<ApiResponse> addAddress(@RequestBody @Valid AddressDto addressDto, HttpServletRequest request) throws AuthenticationFailException {
        User user = helper.getUserFromToken(request);

        try {
//
            addressService.saveAddress(addressDto, user);
            log.info("Address added successfully for user: {}", user.getEmail());
            return new ResponseEntity<>(new ApiResponse(true, "Address added successfully"), HttpStatus.CREATED);
        } catch (Exception e) {
            log.error("Error adding address for user: {}", user.getEmail(), e);
            return new ResponseEntity<>(new ApiResponse(false, "Failed to add address"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @ApiOperation(value = "Update address for a particular user")
    @PutMapping(value = "/update/{addressId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('OWNER', 'BORROWER')")
    public ResponseEntity<ApiResponse> updateAddress(@PathVariable("addressId") Long addressId, @RequestBody @Valid AddressDto addressDto, HttpServletRequest request) throws AuthenticationFailException {
        User user = helper.getUserFromToken(request);

        try {
            Optional<Address> optionalAddress = addressService.readAddress(addressId);
            if (optionalAddress.isEmpty()) {
                log.error("Address not valid for update (addressId: {})", addressId);
                return new ResponseEntity<>(new ApiResponse(false, "Address not valid"), HttpStatus.NOT_FOUND);
            }

            Address checkAddress = addressService.readAddress(user, addressId);
            if (!Helper.notNull(checkAddress)) {
                log.error("Address does not belong to the current user (addressId: {})", addressId);
                return new ResponseEntity<>(new ApiResponse(false, "Address does not belong to the current user"), HttpStatus.FORBIDDEN);
            }

            addressService.updateAddress(addressDto, addressId, user);
            log.info("Address updated successfully for user: {}", user.getEmail());
            return new ResponseEntity<>(new ApiResponse(true, "Address updated successfully"), HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error updating address for user: {}", user.getEmail(), e);
            return new ResponseEntity<>(new ApiResponse(false, "Failed to update address"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @ApiOperation(value = "List all addresses for a particular user")
    @GetMapping(value = "/listAddress", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('OWNER', 'BORROWER')")
    public ResponseEntity<List<Address>> listAddress(HttpServletRequest request) throws AuthenticationFailException {
        User user = helper.getUserFromToken(request);

        try {
            List<Address> addresses = addressService.listAddress(user);
            log.info("List of addresses retrieved for user: {}", user.getEmail());
            return new ResponseEntity<>(addresses, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error listing addresses for user: {}", user.getEmail(), e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @ApiOperation(value = "Delete address for a particular user")
    @DeleteMapping("/delete/{addressId}")
    @PreAuthorize("hasAnyAuthority('OWNER', 'BORROWER')")
    public ResponseEntity<ApiResponse> deleteAddress(@PathVariable("addressId") Long addressId, HttpServletRequest request) throws AuthenticationFailException {
        User user = helper.getUserFromToken(request);

        try {
            Optional<Address> optionalAddress = addressService.readAddress(addressId);
            if (optionalAddress.isEmpty()) {
                log.error("Address is invalid (addressId: {})", addressId);
                return new ResponseEntity<>(new ApiResponse(false, "Address is invalid"), HttpStatus.NOT_FOUND);
            }

            Address checkAddress = addressService.readAddress(user, addressId);
            if (!Helper.notNull(checkAddress)) {
                log.error("Address not found (addressId: {})", addressId);
                return new ResponseEntity<>(new ApiResponse(false, "Address not found"), HttpStatus.OK);
            }

            addressService.deleteAddress(addressId);
            log.info("Address deleted successfully for user: {}", user.getEmail());
            return new ResponseEntity<>(new ApiResponse(true, "Address deleted successfully"), HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error deleting address for user: {}", user.getEmail(), e);
            return new ResponseEntity<>(new ApiResponse(false, "Failed to delete address"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(value = "/getAddressById/{addressId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('OWNER', 'BORROWER')")
    public ResponseEntity<Address> getAddressById(@PathVariable("addressId") Long addressId) {
        try {
            Optional<Address> optionalAddress = addressService.readAddress(addressId);
            if (optionalAddress.isEmpty()) {
                log.error("Address not found (addressId: {})", addressId);
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            Address address = optionalAddress.get();
            log.info("Address retrieved successfully (addressId: {})", addressId);
            return new ResponseEntity<>(address, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error retrieving address (addressId: {})", addressId, e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
