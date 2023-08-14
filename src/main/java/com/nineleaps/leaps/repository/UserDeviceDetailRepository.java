package com.nineleaps.leaps.repository;

import com.nineleaps.leaps.model.User;
import com.nineleaps.leaps.model.UserDeviceDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserDeviceDetailRepository extends JpaRepository<UserDeviceDetail, Long> {

    UserDeviceDetail findByUserAndUniqueDeviceId(User user, String uniqueDeviceId);
}
