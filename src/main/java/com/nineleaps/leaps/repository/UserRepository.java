package com.nineleaps.leaps.repository;

import com.nineleaps.leaps.enums.Role;
import com.nineleaps.leaps.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);

    User findByRole(Role role);

    User findByPhoneNumber(String phoneNumber);
//    @Query("SELECT NEW com.nineleaps.leaps.dto.UserDeviceDetailDTO(u.id, u.email, d.uniqueDeviceId) " +
//            "FROM User u JOIN u.userDeviceDetail d " +
//            "WHERE u.email = :email")
//    UserDeviceDetailDTO findUserDeviceDetailByEmail(@Param("email") String email);

}
