package com.nineleaps.leaps.controller;

import com.nineleaps.leaps.common.ApiResponse;
import com.nineleaps.leaps.model.User;
import com.nineleaps.leaps.model.UserDeviceDetail;
import com.nineleaps.leaps.service.UserDeviceDetailServiceInterface;
import com.nineleaps.leaps.utils.Helper;
import com.nineleaps.leaps.utils.SecurityUtility;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

@RestController
@RequestMapping("/api/v1/user/devicedetail")
@AllArgsConstructor
@Api(tags = "User Device Detail Api", description = "Contains api for managing user device details")
public class UserDeviceDetailController {
    private final UserDeviceDetailServiceInterface userDeviceDetailService;
    private final Helper helper;
    private final SecurityUtility securityUtility;

    @ApiOperation(value = "Api to store user device detail (deviceToken and uniqueDeviceId)")
    @PostMapping("/save")
    public ResponseEntity<ApiResponse> saveUserDeviceDetail(
            @RequestParam(value = "deviceToken") String deviceToken,
            @RequestParam(value = "uniqueDeviceId") String uniqueDeviceId,
            HttpServletRequest request) {
        String authorizationHeader = request.getHeader("Authorization");
        String token = authorizationHeader.substring(7);
        User user = helper.getUser(token);

        if (Objects.nonNull(user)) {
            UserDeviceDetail existingDeviceDetail = userDeviceDetailService.getUserDeviceDetailByUserAndUniqueId(user, uniqueDeviceId);

            if (existingDeviceDetail != null) {
                if (existingDeviceDetail.getUniqueDeviceId().equals(uniqueDeviceId)) {
                    existingDeviceDetail.setDeviceToken(deviceToken);
                    userDeviceDetailService.saveUserDeviceDetail(existingDeviceDetail);
                    return ResponseEntity.ok(new ApiResponse(true, "Device Token Updated for user: " + user.getEmail()));
                } else {
                    userDeviceDetailService.saveDeviceTokenAndUser(deviceToken, uniqueDeviceId, user);
                    return ResponseEntity.ok(new ApiResponse(true, "Device Token and Unique Device Id Added for user: " + user.getEmail()));
                }
            } else {
                userDeviceDetailService.saveDeviceTokenAndUser(deviceToken, uniqueDeviceId, user);
                return ResponseEntity.ok(new ApiResponse(true, "Device Token and Unique Device Id Added for user: " + user.getEmail()));
            }
        } else {
            return ResponseEntity.ok(new ApiResponse(false, "User not found"));
        }
    }
}
