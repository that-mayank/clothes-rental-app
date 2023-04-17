package com.nineleaps.leaps.repository;

import com.nineleaps.leaps.model.Address;
import com.nineleaps.leaps.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {
    List<Address> findAllByUser(User user);
}
