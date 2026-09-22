package com.practice.security;

import java.io.IOException;
import java.util.Collections;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    public JwtAuthenticationFilter(
            JwtService jwtService) {

        this.jwtService = jwtService;
    }

    @Override
    protected boolean shouldNotFilter(
            HttpServletRequest request) {

        String path = request.getServletPath();

        if ("OPTIONS".equalsIgnoreCase(
                request.getMethod())) {
            return true;
        }

        if (path.equals("/api/services")
                || path.startsWith("/api/services/")) {
            return true;
        }

        if (path.equals("/api/providers")
                || path.startsWith("/api/providers/")) {
            return true;
        }

        if (path.equals("/api/users/login")
                || path.equals("/api/users/register")) {
            return true;
        }

        return false;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        String header =
                request.getHeader("Authorization");

        if (header == null
                || !header.startsWith("Bearer ")) {

            filterChain.doFilter(request, response);
            return;
        }

        String token = header.substring(7);

        if (!jwtService.isTokenValid(token)) {

            response.setStatus(
                    HttpServletResponse.SC_UNAUTHORIZED
            );

            response.setContentType(
                    "application/json"
            );

            response.getWriter().write(
                    "{\"error\":\"Invalid or expired token\"}"
            );

            return;
        }

        try {

            String email =
                    jwtService.extractEmail(token);

            String role =
                    jwtService.extractRole(token);

            if (role == null || role.isBlank()) {
                role = "CUSTOMER";
            }

            // Spring Security roles require ROLE_ prefix
            SimpleGrantedAuthority authority =
                    new SimpleGrantedAuthority(
                            "ROLE_" + role
                    );

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            email,
                            null,
                            Collections.singletonList(authority)
                    );

            SecurityContextHolder
                    .getContext()
                    .setAuthentication(authentication);

        } catch (Exception e) {

            SecurityContextHolder.clearContext();

            response.setStatus(
                    HttpServletResponse.SC_UNAUTHORIZED
            );

            response.setContentType(
                    "application/json"
            );

            response.getWriter().write(
                    "{\"error\":\"Invalid token\"}"
            );

            return;
        }

        filterChain.doFilter(request, response);
    }
}