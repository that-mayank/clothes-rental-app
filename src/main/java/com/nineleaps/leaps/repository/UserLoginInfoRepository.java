package com.nineleaps.leaps.repository;

import com.nineleaps.leaps.model.UserLoginInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserLoginInfoRepository extends JpaRepository<UserLoginInfo, Long> {

    UserLoginInfo findByUserId(Long userId);

}

