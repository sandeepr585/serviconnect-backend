package com.practice.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.practice.entity.User;
import com.practice.repository.UserRepository;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // =========================================================
    // REGISTER USER
    // =========================================================

    public User registerUser(User user) {

        if (userRepository.existsByEmail(user.getEmail())) {

            throw new RuntimeException(
                    "Email already registered"
            );
        }

        if (user.getPassword() == null ||
                user.getPassword().isBlank()) {

            throw new RuntimeException(
                    "Password is required"
            );
        }

        // Every new user is CUSTOMER
        user.setRole("CUSTOMER");

        // Encrypt password
        user.setPassword(
                passwordEncoder.encode(
                        user.getPassword()
                )
        );

        return userRepository.save(user);
    }

    // =========================================================
    // LOGIN USER
    // =========================================================

    public User loginUser(
            String email,
            String password) {

        User user = userRepository.findByEmail(email);

        if (user == null) {
            return null;
        }

        if (user.getPassword() == null) {
            return null;
        }

        if (!passwordEncoder.matches(
                password,
                user.getPassword())) {

            return null;
        }

        return user;
    }

    // =========================================================
    // GET USER BY EMAIL
    // =========================================================

    public User getUserByEmail(String email) {

        return userRepository.findByEmail(email);
    }

    // =========================================================
    // GET USER BY ID
    // =========================================================

    public User getUserById(Long id) {

        return userRepository
                .findById(id)
                .orElse(null);
    }

    // =========================================================
    // SAVE USER
    // =========================================================

    public User saveUser(User user) {

        return userRepository.save(user);
    }

    // =========================================================
    // DELETE USER
    // =========================================================

    public boolean deleteUser(Long id) {

        if (!userRepository.existsById(id)) {
            return false;
        }

        userRepository.deleteById(id);

        return true;
    }
}