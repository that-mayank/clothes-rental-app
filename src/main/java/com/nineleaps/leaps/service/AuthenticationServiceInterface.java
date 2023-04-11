package com.nineleaps.leaps.service;

import com.nineleaps.leaps.model.AuthenticationToken;
import com.nineleaps.leaps.model.User;

public interface AuthenticationServiceInterface {
    public void saveToken(AuthenticationToken authenticationToken);
    public AuthenticationToken getToken(User user);

    public void authenticate(String token);

    public User getUser(String token);
}
