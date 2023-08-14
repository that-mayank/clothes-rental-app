package com.nineleaps.leaps.config.filter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nineleaps.leaps.enums.Role;
import com.nineleaps.leaps.exceptions.RuntimeCustomException;
import com.nineleaps.leaps.model.UserDeviceDetail;
import com.nineleaps.leaps.repository.UserDeviceDetailRepository;
import com.nineleaps.leaps.service.UserServiceInterface;
import com.nineleaps.leaps.utils.SecurityUtility;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.stream.Collectors;

@Slf4j
@Getter
@Setter
@AllArgsConstructor
public class CustomAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private final AuthenticationManager authenticationManager;
    private final SecurityUtility securityUtility;
    private final UserServiceInterface userServiceInterface;
    private final UserDeviceDetailRepository userDeviceDetailRepository;


    //Authenticates the user using login credentials - email and password
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        try {
            // Read JSON data from the request
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(request.getInputStream());

            String email = jsonNode.get("email").asText();
            String password = jsonNode.get("password").asText();
            String uniqueDeviceId = jsonNode.get("uniqueDeviceId").asText();
            String[] roles = new String[1];
            roles[0] = userServiceInterface.getUser(email).getRole().toString();

            // Perform authentication and return an Authentication object
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(email, password);


            // Create a CustomUser instance with the uniqueDeviceId
            CustomUser customUser = new CustomUser(email, password, roles, uniqueDeviceId);
            authenticationToken.setDetails(customUser); // Set CustomUser as authentication details

            return authenticationManager.authenticate(authenticationToken);
        } catch (IOException e) {
            throw new RuntimeCustomException("Error occurred while reading JSON data from the request.");
        }
    }

    //generates access and refresh token
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) throws IOException {
        CustomUser customUser = (CustomUser) authentication.getDetails();

        String uniqueDeviceId = customUser.getUniqueDeviceId();

        // Fetch or create UserDeviceDetail for this uniqueDeviceId and user
        UserDeviceDetail userDeviceDetail = userDeviceDetailRepository.findByUserAndUniqueDeviceId(userServiceInterface.getUser(customUser.getUsername()), uniqueDeviceId);

        if (userDeviceDetail == null) {
            userDeviceDetail = new UserDeviceDetail();
            userDeviceDetail.setUser(userServiceInterface.getUser(customUser.getUsername())); // Fetch the User entity by email
            userDeviceDetail.setUniqueDeviceId(uniqueDeviceId);
            userDeviceDetailRepository.save(userDeviceDetail);
        }

        String secretFilePath = "/Desktop"+"/Leaps-Backend"+"/secret"+"/secret.txt";
        String absolutePath = System.getProperty("user.home") + File.separator + secretFilePath;
        String secret = securityUtility.readSecretFromFile(absolutePath);
        Algorithm algorithm = Algorithm.HMAC256(secret.getBytes());
        // Update access token expiration time dynamically
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime accessTokenExpirationTime = now.plusMinutes(2); // Update to desired expiration time 24hrs or one day
        Date accessTokenExpirationDate = Date.from(accessTokenExpirationTime.atZone(ZoneId.systemDefault()).toInstant());
        String accessToken = JWT.create()
                .withSubject(customUser.getUsername())
                .withExpiresAt(accessTokenExpirationDate)
                .withIssuer(request.getRequestURL().toString())
                .withClaim("roles", customUser.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
                .sign(algorithm);

        // Update refresh token expiration time dynamically
        LocalDateTime refreshTokenExpirationTime = now.plusMinutes(43200); // Update to desired expiration time 30 days
        Date refreshTokenExpirationDate = Date.from(refreshTokenExpirationTime.atZone(ZoneId.systemDefault()).toInstant());
        String refreshToken = JWT.create()
                .withSubject(customUser.getUsername())
                .withExpiresAt(refreshTokenExpirationDate)
                .withIssuer(request.getRequestURL().toString())
                .sign(algorithm);
        String email = customUser.getUsername();
        securityUtility.saveAccessToken(email,accessToken,accessTokenExpirationDate,userDeviceDetail.getUniqueDeviceId());
        securityUtility.saveRefreshToken(email,refreshToken,refreshTokenExpirationDate,userDeviceDetail.getUniqueDeviceId());

        response.setHeader("access_token", accessToken);
        response.setHeader("refresh_token", refreshToken);
    }


}