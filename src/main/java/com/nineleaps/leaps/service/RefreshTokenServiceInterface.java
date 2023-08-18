package com.nineleaps.leaps.service;

public interface RefreshTokenServiceInterface {
   String getRefreshToken(String email);
   void deleteRefreshTokenByEmailAndToken(String email,String token);
}
