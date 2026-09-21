package com.practice.config;

import com.practice.security.JwtAuthenticationFilter;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(
            JwtAuthenticationFilter jwtAuthenticationFilter
    ) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http
    ) throws Exception {

        http

            // =========================================
            // CSRF
            // =========================================
            .csrf(csrf -> csrf.disable())

            // =========================================
            // CORS
            // =========================================
            .cors(cors -> {})

            // =========================================
            // STATELESS JWT AUTHENTICATION
            // =========================================
            .sessionManagement(session ->
                session.sessionCreationPolicy(
                    SessionCreationPolicy.STATELESS
                )
            )

            // =========================================
            // AUTHORIZATION
            // =========================================
            .authorizeHttpRequests(auth -> {

                // =========================================
                // PUBLIC ROOT
                // =========================================
                auth.requestMatchers(
                    "/",
                    "/error"
                ).permitAll();

                // =========================================
                // CORS PREFLIGHT
                // =========================================
                auth.requestMatchers(
                    HttpMethod.OPTIONS,
                    "/**"
                ).permitAll();

                // =========================================
                // USER REGISTER + LOGIN
                // =========================================
                auth.requestMatchers(
                    "/api/users/register",
                    "/api/users/login"
                ).permitAll();

                // =========================================
                // PUBLIC SERVICES
                // =========================================
                auth.requestMatchers(
                    HttpMethod.GET,
                    "/api/services/**"
                ).permitAll();

                // =========================================
                // PUBLIC PROVIDERS
                // =========================================
                auth.requestMatchers(
                    HttpMethod.GET,
                    "/api/providers/**"
                ).permitAll();

                // =========================================
                // PUBLIC PROVIDER REVIEWS
                // =========================================
                auth.requestMatchers(
                    HttpMethod.GET,
                    "/api/reviews/provider/**"
                ).permitAll();

                // =========================================
                // ADMIN - CREATE PROVIDER
                // =========================================
                auth.requestMatchers(
                    HttpMethod.POST,
                    "/api/providers"
                ).hasRole("ADMIN");

                // =========================================
                // CUSTOMER / ADMIN - CREATE BOOKING
                // =========================================
                auth.requestMatchers(
                    HttpMethod.POST,
                    "/api/bookings"
                ).hasAnyRole(
                    "CUSTOMER",
                    "ADMIN"
                );

                // =========================================
                // CUSTOMER / ADMIN - MY BOOKINGS
                // =========================================
                auth.requestMatchers(
                    HttpMethod.GET,
                    "/api/bookings/my-bookings"
                ).hasAnyRole(
                    "CUSTOMER",
                    "ADMIN"
                );

                // =========================================
                // ADMIN - ALL BOOKINGS
                // =========================================
                auth.requestMatchers(
                    HttpMethod.GET,
                    "/api/bookings"
                ).hasRole("ADMIN");

                // =========================================
                // ADMIN - ASSIGN PROVIDER
                // =========================================
                auth.requestMatchers(
                    HttpMethod.PUT,
                    "/api/bookings/*/assign/*"
                ).hasRole("ADMIN");

                // =========================================
                // PROVIDER / ADMIN - PROVIDER BOOKINGS
                // =========================================
                auth.requestMatchers(
                    HttpMethod.GET,
                    "/api/bookings/provider/**"
                ).hasAnyRole(
                    "PROVIDER",
                    "ADMIN"
                );

                // =========================================
                // PROVIDER / ADMIN - UPDATE STATUS
                // =========================================
                auth.requestMatchers(
                    HttpMethod.PUT,
                    "/api/bookings/*/status"
                ).hasAnyRole(
                    "PROVIDER",
                    "ADMIN"
                );

                // =========================================
                // CUSTOMER / ADMIN - CANCEL BOOKING
                // =========================================
                auth.requestMatchers(
                    HttpMethod.PUT,
                    "/api/bookings/*/cancel"
                ).hasAnyRole(
                    "CUSTOMER",
                    "ADMIN"
                );

                // =========================================
                // CUSTOMER / ADMIN - CREATE REVIEW
                // =========================================
                auth.requestMatchers(
                    HttpMethod.POST,
                    "/api/reviews/booking/**"
                ).hasAnyRole(
                    "CUSTOMER",
                    "ADMIN"
                );

                // =========================================
                // CUSTOMER / ADMIN - MY REVIEWS
                // =========================================
                auth.requestMatchers(
                    HttpMethod.GET,
                    "/api/reviews/my-reviews"
                ).hasAnyRole(
                    "CUSTOMER",
                    "ADMIN"
                );

                // =========================================
                // AUTHENTICATED - BOOKING REVIEWS
                // =========================================
                auth.requestMatchers(
                    HttpMethod.GET,
                    "/api/reviews/booking/**"
                ).authenticated();

                // =========================================
                // EVERYTHING ELSE
                // =========================================
                auth.anyRequest().authenticated();
            })

            // =========================================
            // JWT FILTER
            // =========================================
            .addFilterBefore(
                jwtAuthenticationFilter,
                UsernamePasswordAuthenticationFilter.class
            );

        return http.build();
    }
}