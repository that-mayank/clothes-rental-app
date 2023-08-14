package com.nineleaps.leaps.service;

import com.nineleaps.leaps.model.User;
import com.nineleaps.leaps.model.UserDeviceDetail;
import com.nineleaps.leaps.repository.UserDeviceDetailRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserDeviceDetailServiceImppl implements UserDeviceDetailServiceInterface {

    private final UserDeviceDetailRepository userDeviceDetailRepository;

    @Override
    public UserDeviceDetail getUserDeviceDetailByUserAndUniqueId(User user, String uniqueDeviceId) {
        return userDeviceDetailRepository.findByUserAndUniqueDeviceId(user, uniqueDeviceId);
    }

    @Override
    public void saveUserDeviceDetail(UserDeviceDetail userDeviceDetail) {
        userDeviceDetailRepository.save(userDeviceDetail);
    }

    @Override
    public void saveDeviceTokenAndUser(String deviceToken, String uniqueDeviceId, User user) {
        UserDeviceDetail newDeviceDetail = new UserDeviceDetail(uniqueDeviceId, user);
        userDeviceDetailRepository.save(newDeviceDetail);
    }
}
