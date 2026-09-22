package com.practice.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.practice.security.JwtAuthenticationFilter;

@Configuration
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(
            JwtAuthenticationFilter jwtAuthenticationFilter) {

        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    // =========================================================
    // CORS
    // =========================================================

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration configuration =
                new CorsConfiguration();

        configuration.setAllowedOriginPatterns(
                List.of(
                        "https://*.vercel.app",
                        "http://localhost:5173",
                        "http://localhost:5174",
                        "http://localhost:3000"
                )
        );

        configuration.setAllowedMethods(
                List.of(
                        "GET",
                        "POST",
                        "PUT",
                        "DELETE",
                        "PATCH",
                        "OPTIONS"
                )
        );

        configuration.setAllowedHeaders(
                List.of("*")
        );

        configuration.setExposedHeaders(
                List.of("Authorization")
        );

        // JWT is sent in Authorization header.
        configuration.setAllowCredentials(false);

        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration(
                "/**",
                configuration
        );

        return source;
    }

    // =========================================================
    // SECURITY FILTER CHAIN
    // =========================================================

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http) throws Exception {

        http

            // Disable CSRF because this is a JWT REST API
            .csrf(csrf -> csrf.disable())

            // Enable CORS
            .cors(cors ->
                    cors.configurationSource(
                            corsConfigurationSource()
                    )
            )

            // JWT = stateless
            .sessionManagement(session ->
                    session.sessionCreationPolicy(
                            SessionCreationPolicy.STATELESS
                    )
            )

            // =================================================
            // AUTHORIZATION
            // =================================================

            .authorizeHttpRequests(auth -> auth

                // CORS preflight
                .requestMatchers(
                        HttpMethod.OPTIONS,
                        "/**"
                ).permitAll()

                // Root / error
                .requestMatchers(
                        "/",
                        "/error"
                ).permitAll()

                // =================================================
                // LOGIN / REGISTER
                // =================================================

                .requestMatchers(
                        HttpMethod.POST,
                        "/api/users/register",
                        "/api/users/login"
                ).permitAll()

                // =================================================
                // SERVICES - PUBLIC GET
                // =================================================

                .requestMatchers(
                        HttpMethod.GET,
                        "/api/services",
                        "/api/services/**"
                ).permitAll()

                // =================================================
                // PROVIDERS - PUBLIC GET
                // =================================================

                .requestMatchers(
                        HttpMethod.GET,
                        "/api/providers",
                        "/api/providers/**"
                ).permitAll()

                // =================================================
                // REVIEWS - PUBLIC GET
                // =================================================

                .requestMatchers(
                        HttpMethod.GET,
                        "/api/reviews/provider/**"
                ).permitAll()

                // =================================================
                // CREATE PROVIDER - ADMIN
                // =================================================

                .requestMatchers(
                        HttpMethod.POST,
                        "/api/providers"
                ).hasRole("ADMIN")

                // =================================================
                // BOOKINGS
                // =================================================

                // Customer / Admin can create booking
                .requestMatchers(
                        HttpMethod.POST,
                        "/api/bookings"
                ).hasAnyRole(
                        "CUSTOMER",
                        "ADMIN"
                )

                // Customer / Admin can see own bookings
                .requestMatchers(
                        HttpMethod.GET,
                        "/api/bookings/my-bookings"
                ).hasAnyRole(
                        "CUSTOMER",
                        "ADMIN"
                )

                // Only Admin can see all bookings
                .requestMatchers(
                        HttpMethod.GET,
                        "/api/bookings"
                ).hasRole("ADMIN")

                // Admin assigns provider
                .requestMatchers(
                        HttpMethod.PUT,
                        "/api/bookings/*/assign/*"
                ).hasRole("ADMIN")

                // Provider / Admin can see provider bookings
                .requestMatchers(
                        HttpMethod.GET,
                        "/api/bookings/provider/**"
                ).hasAnyRole(
                        "PROVIDER",
                        "ADMIN"
                )

                // Provider / Admin can update booking status
                .requestMatchers(
                        HttpMethod.PUT,
                        "/api/bookings/*/status"
                ).hasAnyRole(
                        "PROVIDER",
                        "ADMIN"
                )

                // Customer / Admin can cancel booking
                .requestMatchers(
                        HttpMethod.PUT,
                        "/api/bookings/*/cancel"
                ).hasAnyRole(
                        "CUSTOMER",
                        "ADMIN"
                )

                // =================================================
                // REVIEWS
                // =================================================

                // Customer / Admin can create review
                .requestMatchers(
                        HttpMethod.POST,
                        "/api/reviews/booking/**"
                ).hasAnyRole(
                        "CUSTOMER",
                        "ADMIN"
                )

                // Customer / Admin can see own reviews
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

                // =================================================
                // EVERYTHING ELSE
                // =================================================

                .anyRequest().authenticated()
            )

            // =================================================
            // JWT FILTER
            // =================================================

            .addFilterBefore(
                    jwtAuthenticationFilter,
                    UsernamePasswordAuthenticationFilter.class
            );

        return http.build();
    }
}