package com.nineleaps.leaps.service;

import com.nineleaps.leaps.model.User;
import com.nineleaps.leaps.model.UserDeviceDetail;

public interface UserDeviceDetailServiceInterface {

    UserDeviceDetail getUserDeviceDetailByUserAndUniqueId(User user, String uniqueDeviceId);

    void saveUserDeviceDetail(UserDeviceDetail userDeviceDetail);

    void saveDeviceTokenAndUser(String deviceToken, String uniqueDeviceId, User user);
}
