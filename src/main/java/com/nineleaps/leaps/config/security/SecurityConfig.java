package com.nineleaps.leaps.config.security;

import com.nineleaps.leaps.config.filter.CustomAuthenticationFilter;
import com.nineleaps.leaps.config.filter.CustomAuthorizationFilter;
import com.nineleaps.leaps.repository.RefreshTokenRepository;
import com.nineleaps.leaps.utils.SecurityUtility;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenRepository refreshTokenRepository;
    private final SecurityUtility securityUtility;
    private  final String ROLE_OWNER = "OWNER";
    private  final String ROLE_BORROWER = "BORROWER";
    private  final String ROLE_GUEST = "GUEST";
    private final String ROLE_ADMIN = "ADMIN";

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        CustomAuthenticationFilter customAuthenticationFilter = new CustomAuthenticationFilter(authenticationManagerBean(), securityUtility, refreshTokenRepository);
        customAuthenticationFilter.setFilterProcessesUrl("/api/v1/login");
        http
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                .antMatchers(
                        "/api/v1/phoneNo",
                        "/api/v1/otp",
                        "/api/v1/user/signup",
                        "/api/v1/user/refreshtoken"
                ).permitAll()
                .antMatchers("/api/v1/file/view/**").permitAll()
                .antMatchers(
                        "/api/v1/category/list",
                        "/api/v1/subcategory/list",
                        "/api/v1/product/search",
                        "/api/v1/product/list",
                        "/api/v1/product/listByPriceRange"
                ).hasAnyAuthority(ROLE_OWNER, ROLE_BORROWER, ROLE_GUEST )
                .antMatchers(HttpMethod.GET, "/api/v1/users").hasAuthority(ROLE_ADMIN)
                .antMatchers(
                        "/api/v1/address/add",
                        "/api/v1/address/update/**",
                        "/api/v1/address/listaddress",
                        "/api/v1/address/delete/**"
                ).hasAnyAuthority(ROLE_OWNER, ROLE_BORROWER)
                .antMatchers(
                        "/api/v1/cart/add",
                        "/api/v1/cart/list",
                        "/api/v1/cart/update",
                        "/api/v1/cart/delete/**"
                ).hasAnyAuthority(ROLE_OWNER, ROLE_BORROWER)
                .antMatchers(
                        "/api/v1/category/create",
                        "/api/v1/category/update/**"
                ).permitAll()
                .antMatchers(
                        "/api/v1/order/create-checkout-session",
                        "/api/v1/order/add",
                        "/api/v1/order/list",
                        "/api/v1/order/getOrderById/**",
                        "/api/v1/user/update"
                ).hasAnyAuthority(ROLE_BORROWER, ROLE_OWNER)
                .antMatchers(
                        "/api/v1/dashboard/owner-view",
                        "/api/v1/dashboard/analytics"
                ).hasAuthority(ROLE_OWNER)
                .and()
                .addFilter(customAuthenticationFilter)
                .addFilterBefore(new CustomAuthorizationFilter(securityUtility), UsernamePasswordAuthenticationFilter.class);
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
}
