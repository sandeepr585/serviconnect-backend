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
                .csrf(csrf ->
                        csrf.disable()
                )

                .cors(cors -> {})

                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                )

                .authorizeHttpRequests(auth -> auth

                        // Public root endpoint
                        .requestMatchers(
                                "/"
                        ).permitAll()

                        // Allow CORS preflight requests
                        .requestMatchers(
                                HttpMethod.OPTIONS,
                                "/**"
                        ).permitAll()

                        // User registration and login
                        .requestMatchers(
                                "/api/users/register",
                                "/api/users/login"
                        ).permitAll()

                        // Public services
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/services/**"
                        ).permitAll()

                        // Public providers
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/providers/**"
                        ).permitAll()

                        // Public provider reviews
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/reviews/provider/**"
                        ).permitAll()

                        // Admin creates providers
                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/providers"
                        ).hasRole("ADMIN")

                        // Customer/Admin creates bookings
                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/bookings"
                        ).hasAnyRole(
                                "CUSTOMER",
                                "ADMIN"
                        )

                        // Customer/Admin sees own bookings
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/bookings/my-bookings"
                        ).hasAnyRole(
                                "CUSTOMER",
                                "ADMIN"
                        )

                        // Admin sees all bookings
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/bookings"
                        ).hasRole("ADMIN")

                        // Admin assigns provider
                        .requestMatchers(
                                HttpMethod.PUT,
                                "/api/bookings/*/assign/*"
                        ).hasRole("ADMIN")

                        // Provider/Admin sees provider bookings
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/bookings/provider/**"
                        ).hasAnyRole(
                                "PROVIDER",
                                "ADMIN"
                        )

                        // Provider/Admin updates booking status
                        .requestMatchers(
                                HttpMethod.PUT,
                                "/api/bookings/*/status"
                        ).hasAnyRole(
                                "PROVIDER",
                                "ADMIN"
                        )

                        // Customer/Admin cancels booking
                        .requestMatchers(
                                HttpMethod.PUT,
                                "/api/bookings/*/cancel"
                        ).hasAnyRole(
                                "CUSTOMER",
                                "ADMIN"
                        )

                        // Customer/Admin creates review
                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/reviews/booking/**"
                        ).hasAnyRole(
                                "CUSTOMER",
                                "ADMIN"
                        )

                        // Customer/Admin sees own reviews
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/reviews/my-reviews"
                        ).hasAnyRole(
                                "CUSTOMER",
                                "ADMIN"
                        )

                        // Authenticated users can see booking reviews
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/reviews/booking/**"
                        ).authenticated()

                        // Everything else requires authentication
                        .anyRequest().authenticated()
                )

                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }
}