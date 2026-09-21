package com.practice.service;

import org.springframework.security.crypto.password.PasswordEncoder;

import com.practice.entity.User;
import com.practice.repository.UserRepository;

@org.springframework.stereotype.Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User registerUser(User user) {

        if (userRepository.existsByEmail(user.getEmail())) {

            throw new RuntimeException(
                    "Email already registered"
            );
        }

        // Every newly registered account is a CUSTOMER
        user.setRole("CUSTOMER");

        user.setPassword(
                passwordEncoder.encode(
                        user.getPassword()
                )
        );

        return userRepository.save(user);
    }

    public User loginUser(
            String email,
            String password) {

        User user =
                userRepository.findByEmail(email)
                        .orElse(null);

        if (user == null) {
            return null;
        }

        if (!passwordEncoder.matches(
                password,
                user.getPassword())) {

            return null;
        }

        return user;
    }

    public User getUserByEmail(String email) {

        return userRepository
                .findByEmail(email)
                .orElse(null);
    }
}