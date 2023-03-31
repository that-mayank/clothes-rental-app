package com.nineleaps.leaps.service;

import com.nineleaps.leaps.model.AuthenticationToken;
import com.nineleaps.leaps.model.User;

public interface AuthenticationServiceInterface {
    public void saveToken(AuthenticationToken authenticationToken);
    public AuthenticationToken getToken(User user);
}
