package com.nineleaps.leaps.controller;

import com.nineleaps.leaps.common.ApiResponse;
import com.nineleaps.leaps.dto.AddressDto;
import com.nineleaps.leaps.exceptions.AddressOwnershipException;
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
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/address")
@AllArgsConstructor
@Validated
@Api(tags = "Address Api")
public class AddressController {

    //Linking layers using constructor injection

    private final AddressServiceInterface addressService;
    private final Helper helper;

    // API : To add address for particular user
    @ApiOperation(value = "API : To add address for particular user")
    @PostMapping(value = "/add", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('BORROWER')")
    public ResponseEntity<ApiResponse> addAddress(
            @RequestBody @Valid AddressDto addressDto,
            HttpServletRequest request
    ) {

        // JWT : Extracting user info from token

        User user = helper.getUser(request);

        //Calling service layer to add address using Dto

        addressService.saveAddress(addressDto, user);
        return new ResponseEntity<>(
                new ApiResponse(
                        true,
                        "Address added successfully"
                ),
                HttpStatus.CREATED
        );
    }

    // API : To update address for particular user
    @ApiOperation(value = "API : To update address for particular user")
    @PutMapping(value = "/update/{addressId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('BORROWER')")
    public ResponseEntity<ApiResponse> updateAddress(
            @PathVariable("addressId") Long addressId,
            @RequestBody @Valid AddressDto addressDto,
            HttpServletRequest request
    ) {

        // JWT : Extracting user info from token

        User user = helper.getUser(request);

        // Guard Statement : Check if address is present in DB or not

        Optional<Address> optionalAddress = addressService.readAddress(addressId);
        if (optionalAddress.isEmpty()) {
            return new ResponseEntity<>(
                    new ApiResponse(
                            false,
                            "Address not valid"
                    ),
                    HttpStatus.NOT_FOUND
            );
        }

        // Guard Statement : Check if address belong to given user

        Address checkAddress = addressService.readAddress(user, addressId);
        if (!Helper.notNull(checkAddress)) {
            return new ResponseEntity<>(
                    new ApiResponse(
                            false,
                            "Address does not belong to current user"
                    ),
                    HttpStatus.FORBIDDEN
            );
        }

        // Calling service layer to update address using Dto

        addressService.updateAddress(addressDto, addressId, user);
        return new ResponseEntity<>(
                new ApiResponse(
                        true,
                        "Address updated successfully"
                ),
                HttpStatus.OK
        );
    }

    // API : To list all addresses for particular user
    @ApiOperation(value = "API : To list all addresses for particular user")
    @GetMapping(value = "/list", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('BORROWER')")
    public ResponseEntity<List<Address>> listAddress(
            HttpServletRequest request
    ) {

        // JWT : Extracting user info from token

        User user = helper.getUser(request);

        // Calling service layer to return all address

        List<Address> body = addressService.listAddress(user);
        return new ResponseEntity<>(
                body,
                HttpStatus.OK
        );
    }

    // API : To delete address for particular user
    @ApiOperation(value = "API : To delete address for particular user")
    @DeleteMapping("/delete/{addressId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAuthority('BORROWER')")
    public ResponseEntity<ApiResponse> deleteAddress(
            @PathVariable("addressId") Long addressId,
            HttpServletRequest request
    ) {

        // JWT : Extracting user info from token

        User user = helper.getUser(request);

        // Guard Statement : Check if address is present in DB or not

        Optional<Address> optionalAddress = addressService.readAddress(addressId);
        if (optionalAddress.isEmpty()) {
            return new ResponseEntity<>(
                    new ApiResponse(
                            false,
                            "Address is invalid"
                    ),
                    HttpStatus.NOT_FOUND
            );
        }

        // Guard Statement : Check if address belong to given user

        Address checkAddress = addressService.readAddress(user, addressId);
        if (!Helper.notNull(checkAddress)) {
            return new ResponseEntity<>(
                    new ApiResponse(
                            false,
                            "Address do not belong to the current user"
                    ),
                    HttpStatus.FORBIDDEN
            );
        }

        // Calling service layer to delete address

        addressService.deleteAddress(addressId);
        return new ResponseEntity<>(
                new ApiResponse(
                        true,
                        "Address deleted successfully"
                ),
                HttpStatus.NO_CONTENT
        );
    }

    // API : To get address for particular user by address id
    @ApiOperation(value = "API : To get address for particular user by address id")
    @GetMapping(value = "/getAddressById/{addressId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('BORROWER')")
    public ResponseEntity<Address> getAddressById(
            @PathVariable("addressId") Long addressId,
            HttpServletRequest request
    ) {

        // JWT : Extracting user info from token

        User user = helper.getUser(request);

        // Guard Statement : Check if address is present in DB or not

        Optional<Address> optionalAddress = addressService.readAddress(addressId);
        if (optionalAddress.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        // Guard Statement : Check if address belong to given user

        Address checkAddress = addressService.readAddress(user, addressId);
        if (!Helper.notNull(checkAddress)) {
            throw new AddressOwnershipException("Address do not belong to the current user");
        }

        // Retrieve address from optional object

        Address address = optionalAddress.get();
        return new ResponseEntity<>(
                address,
                HttpStatus.OK
        );
    }
}

