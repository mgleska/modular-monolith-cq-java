package com.mgleska.mmcqjava2.shared;

import com.mgleska.mmcqjava2.customer.action.command.ValidateAccessTokenCmd;
import com.mgleska.mmcqjava2.user.action.command.ValidateUserTokenCmd;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public UserDetailsService userDetailsService() {
        return new InMemoryUserDetailsManager();
    }

    @Bean
    @Order(1)
    public SecurityFilterChain noAuth(HttpSecurity http) {
        http
            .securityMatcher("/api/customer/login", "/api/admin/user/login")
            .authorizeHttpRequests(authorize -> authorize.anyRequest().permitAll())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .csrf(AbstractHttpConfigurer::disable)
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
        ;
        return http.build();
    }

    @Bean
    @Order(2)
    public SecurityFilterChain userAuth(HttpSecurity http, ValidateUserTokenCmd validateUserTokenCmd, CustomExceptionHandler exceptionHandler) {
        http
            .securityMatcher("/api/admin/**")
            .authorizeHttpRequests(authorize -> authorize.anyRequest().hasRole("USER"))
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .csrf(AbstractHttpConfigurer::disable)
            .addFilterBefore(new UserAuthFilter(validateUserTokenCmd, exceptionHandler), LogoutFilter.class)
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
        ;
        return http.build();
    }

    @Bean
    @Order(3)
    public SecurityFilterChain securityFilterChain(HttpSecurity http, ValidateAccessTokenCmd validateAccessTokenCmd, CustomExceptionHandler exceptionHandler) {
        http
            .securityMatcher("/api/**")
            .authorizeHttpRequests(authorize -> authorize.anyRequest().authenticated())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .csrf(AbstractHttpConfigurer::disable)
            .addFilterBefore(new CustomerAuthFilter(validateAccessTokenCmd, exceptionHandler), LogoutFilter.class)
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
        ;
        return http.build();
    }

    private UrlBasedCorsConfigurationSource corsConfigurationSource() {
        var configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("*"));
        configuration.setAllowedMethods(Arrays.asList("GET","POST"));
        configuration.setMaxAge(86400L);
        var source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", configuration);
        return source;
    }
}


class CustomerAuthFilter implements Filter {

    private final ValidateAccessTokenCmd validateAccessTokenCmd;
    private final CustomExceptionHandler exceptionHandler;

    public CustomerAuthFilter(ValidateAccessTokenCmd validateAccessTokenCmd, CustomExceptionHandler exceptionHandler) {
        this.validateAccessTokenCmd = validateAccessTokenCmd;
        this.exceptionHandler = exceptionHandler;
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        var httpRequest = (HttpServletRequest) request;
        var httpResponse = (HttpServletResponse) response;
        var header = httpRequest.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            try {
                SecurityContextHolder.getContext().setAuthentication(this.validateAccessTokenCmd.validate(header.substring(7)));
            } catch (RuntimeException ex) {
                this.exceptionHandler.handleFilterException(httpRequest, httpResponse, ex);
                return;
            }
        }

        chain.doFilter(request, response);
    }
}

class UserAuthFilter implements Filter {

    private final ValidateUserTokenCmd validateUserTokenCmd;
    private final CustomExceptionHandler exceptionHandler;

    public UserAuthFilter(ValidateUserTokenCmd validateUserTokenCmd, CustomExceptionHandler exceptionHandler) {
        this.validateUserTokenCmd = validateUserTokenCmd;
        this.exceptionHandler = exceptionHandler;
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        var httpRequest = (HttpServletRequest) request;
        var httpResponse = (HttpServletResponse) response;
        var header = httpRequest.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            try {
                SecurityContextHolder.getContext().setAuthentication(this.validateUserTokenCmd.validate(header.substring(7)));
            } catch (RuntimeException ex) {
                this.exceptionHandler.handleFilterException(httpRequest, httpResponse, ex);
                return;
            }
        }

        chain.doFilter(request, response);
    }
}

