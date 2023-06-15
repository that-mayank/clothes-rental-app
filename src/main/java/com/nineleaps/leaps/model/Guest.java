package com.nineleaps.leaps.model;

import com.nineleaps.leaps.enums.Role;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue(value = "guest")
@Getter
@Setter
public class Guest extends User {
    public Guest() {
        this.setRole(Role.GUEST);
    }
}
