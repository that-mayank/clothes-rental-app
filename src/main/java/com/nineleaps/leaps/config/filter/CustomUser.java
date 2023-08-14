package com.nineleaps.leaps.config.filter;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;


public class CustomUser extends User {

    private String uniqueDeviceId;

    public CustomUser(String username, String password, String[] roles, String uniqueDeviceId) {
        super(username, password, mapRolesToAuthorities(roles));
        this.uniqueDeviceId = uniqueDeviceId;
    }

    public String getUniqueDeviceId() {
        return uniqueDeviceId;
    }

    public void setUniqueDeviceId(String uniqueDeviceId) {
        this.uniqueDeviceId = uniqueDeviceId;
    }

    // Utility method to convert String roles to GrantedAuthority
    private static Collection<? extends GrantedAuthority> mapRolesToAuthorities(String[] roles) {
        return Arrays.stream(roles)
                .map(role -> new SimpleGrantedAuthority(role))
                .collect(Collectors.toList());
    }

    // Add other constructors and methods as needed
}

