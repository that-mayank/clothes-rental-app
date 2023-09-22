package com.nineleaps.leaps.model;

import com.nineleaps.leaps.enums.Role;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * Represents a guest user in the system.
 * Extends the User class and sets the role to GUEST.
 */
@Entity
@DiscriminatorValue(value = "guest")
@Getter
@Setter
public class Guest extends User {

    /**
     * Initializes a Guest user with the role set to GUEST.
     */
    public Guest() {
        // Set the role of this user as GUEST
        this.setRole(Role.GUEST);
    }
}
