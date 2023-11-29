package com.nineleaps.leaps.controller;

import com.nineleaps.leaps.common.ApiResponse;
import com.nineleaps.leaps.dto.AddressDto;
import com.nineleaps.leaps.model.Address;
import com.nineleaps.leaps.service.AddressServiceInterface;
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

@RestController
@AllArgsConstructor
@Validated
@Api(tags = "Address Api")
@RequestMapping("/api/v1/address")
public class AddressController {

    //Linking layers using constructor injection
    private final AddressServiceInterface addressService;

    // API : To add address for particular user
    @ApiOperation(value = "API : To add address for particular user")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('BORROWER')")
    public ResponseEntity<ApiResponse> addAddress(@RequestBody @Valid AddressDto addressDto, HttpServletRequest request) {
        // Calling service layer to add address using Dto
        addressService.saveAddress(addressDto, request);
        return new ResponseEntity<>(new ApiResponse(true, "Address added successfully"), HttpStatus.CREATED);
    }

    // API : To update address for particular user
    @ApiOperation(value = "API : To update address for particular user")
    @PutMapping(value = "{addressId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('BORROWER')")
    public ResponseEntity<ApiResponse> updateAddress(@PathVariable("addressId") Long addressId, @RequestBody @Valid AddressDto addressDto, HttpServletRequest request) {
        // Calling service layer to update address using Dto
        addressService.updateAddress(addressDto, addressId, request);
        return new ResponseEntity<>(new ApiResponse(true, "Address updated successfully"), HttpStatus.OK);
    }

    // API : To list all addresses for particular user
    @ApiOperation(value = "API : To list all addresses for particular user")
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyAuthority('BORROWER','OWNER')")
    public ResponseEntity<List<Address>> listAddress(HttpServletRequest request) {
        // Calling service layer to return all address
        List<Address> body = addressService.listAddress(request);
        return new ResponseEntity<>(body, HttpStatus.OK);
    }

    // API : To delete address for particular user
    @ApiOperation(value = "API : To delete address for particular user")
    @DeleteMapping("{addressId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAuthority('BORROWER')")
    public ResponseEntity<ApiResponse> deleteAddress(@PathVariable("addressId") Long addressId, HttpServletRequest request) {
        // Calling service layer to delete address
        addressService.deleteAddress(request, addressId);
        return new ResponseEntity<>(new ApiResponse(true, "Address deleted successfully"), HttpStatus.NO_CONTENT);
    }

    // API : To get address for particular user by address id
    @ApiOperation(value = "API : To get address for particular user by address id")
    @GetMapping(value = "{addressId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('BORROWER')")
    public ResponseEntity<Address> getAddressById(@PathVariable("addressId") Long addressId, HttpServletRequest request) {
        Address address = addressService.readAddress(request, addressId);
        return new ResponseEntity<>(address, HttpStatus.OK);
    }
}
