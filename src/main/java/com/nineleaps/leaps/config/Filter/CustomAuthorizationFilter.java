package com.nineleaps.leaps.config.Filter;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nineleaps.leaps.repository.RefreshTokenRepository;
import com.nineleaps.leaps.service.UserService;
import com.nineleaps.leaps.utils.SecurityUtility;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import static java.util.Arrays.stream;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
@Slf4j
public class CustomAuthorizationFilter extends OncePerRequestFilter {
    private final UserService userService;
    private final RefreshTokenRepository refreshTokenRepository;
    private final SecurityUtility securityUtility;
    public static String token_header;
    private final CustomAuthenticationFilter customAuthenticationFilter;
    public CustomAuthorizationFilter(UserService userService, RefreshTokenRepository refreshTokenRepository, SecurityUtility securityUtility, CustomAuthenticationFilter customAuthenticationFilter) {
        this.userService = userService;
        this.refreshTokenRepository = refreshTokenRepository;
        this.securityUtility = securityUtility;
        this.customAuthenticationFilter = customAuthenticationFilter;
    }
    // method for authorizing api requests to respective person
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (request.getServletPath().equals("/api/login")) {
            filterChain.doFilter(request, response);
        } else {
            String authorizationHeader = request.getHeader(AUTHORIZATION);
            if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                try {
                    String token = authorizationHeader.substring("Bearer ".length());
                    if(token.equals(customAuthenticationFilter.getRefresh_token())){
                        response.getWriter().write("refresh token found - updating access token");
                        DecodedJWT decodedAccessToken = JWT.decode(token);
                        String email = decodedAccessToken.getSubject();
//                        System.out.println(email);
                        String access_token= securityUtility.updateAccessToken(email,request);
                        response.setHeader("access_token",access_token);
                        response.getWriter().write("access_token updated in header");
                    }else{
                        if(!securityUtility.isAccessTokenExpired(token)) {
                            Algorithm algorithm = Algorithm.HMAC256("secret".getBytes());
                            JWTVerifier verifier = JWT.require(algorithm).build();
                            DecodedJWT decodedJWT = verifier.verify(token);
                            String email = decodedJWT.getSubject();
                            String[] roles = decodedJWT.getClaim("roles").asArray(String.class);
                            Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
                            stream(roles).forEach(role -> {
                                authorities.add(new SimpleGrantedAuthority(role));
                            });
                            UsernamePasswordAuthenticationToken authenticationToken =
                                    new UsernamePasswordAuthenticationToken(email, null, authorities);
                            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                            token_header = token;
                            filterChain.doFilter(request, response);
                        }else{
                            DecodedJWT decodedAccessToken = JWT.decode(token);
                            String email = decodedAccessToken.getSubject();
//                            System.out.println(email);
                            String access_token = securityUtility.updateAccessToken(email,request);
                            response.setHeader("access_token",access_token);
                        }
                    }
                } catch (Exception exception) {
//                    log.error("error logging in:{}",exception.getMessage());
//                    response.setHeader("error",exception.getMessage());
                    exception.printStackTrace();
                    response.setStatus(FORBIDDEN.value());
                    Map<String,String> error = new HashMap<>();
                    error.put("error_message",exception.getMessage());
                    response.setContentType(APPLICATION_JSON_VALUE);
                    new ObjectMapper().writeValue(response.getOutputStream(),error);
                }
            } else {
                filterChain.doFilter(request, response);
            }
        }
    }
}