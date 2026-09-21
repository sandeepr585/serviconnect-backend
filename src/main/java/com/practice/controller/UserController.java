package com.practice.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.practice.entity.User;
import com.practice.security.JwtService;
import com.practice.service.UserService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(
    origins = {
        "http://localhost:5173",
        "http://localhost:5174"
    }
)
public class UserController {

    private final UserService userService;
    private final JwtService jwtService;

    public UserController(
            UserService userService,
            JwtService jwtService) {

        this.userService = userService;
        this.jwtService = jwtService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(
            @RequestBody User user) {

        try {

            User savedUser =
                    userService.registerUser(user);

            // Never send password back to frontend
            savedUser.setPassword(null);

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(savedUser);

        } catch (RuntimeException e) {

            return ResponseEntity
                    .badRequest()
                    .body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(
            @RequestBody User user) {

        User loggedInUser =
                userService.loginUser(
                        user.getEmail(),
                        user.getPassword()
                );

        if (loggedInUser == null) {

            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid email or password");
        }

        /*
         * Generate JWT using the complete user.
         *
         * JwtService now puts both:
         * email
         * role
         *
         * into the JWT.
         */
        String token =
                jwtService.generateToken(
                        loggedInUser
                );

        // Never send password back to frontend
        loggedInUser.setPassword(null);

        return ResponseEntity.ok(
                new LoginResponse(
                        token,
                        loggedInUser
                )
        );
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(
            HttpServletRequest request) {

        String authorizationHeader =
                request.getHeader("Authorization");

        if (authorizationHeader == null ||
                !authorizationHeader.startsWith("Bearer ")) {

            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("Token missing");
        }

        String token =
                authorizationHeader.substring(7);

        /*
         * Check whether token is valid before
         * extracting information from it.
         */
        if (!jwtService.isTokenValid(token)) {

            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid or expired token");
        }

        String email =
                jwtService.extractEmail(token);

        User user =
                userService.getUserByEmail(email);

        if (user == null) {

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("User not found");
        }

        // Never send password back to frontend
        user.setPassword(null);

        return ResponseEntity.ok(user);
    }

    /*
     * LoginResponse is already inside UserController.
     *
     * We do NOT need a separate LoginResponse.java file.
     */
    public static class LoginResponse {

        private String token;
        private User user;

        public LoginResponse(
                String token,
                User user) {

            this.token = token;
            this.user = user;
        }

        public String getToken() {

            return token;
        }

        public User getUser() {

            return user;
        }
    }
}