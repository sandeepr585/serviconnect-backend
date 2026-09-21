package com.practice.repository;

import com.practice.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository
        extends JpaRepository<Review, Long> {

    Optional<Review> findByBookingId(Long bookingId);

    List<Review> findByProviderId(Long providerId);

    List<Review> findByUserEmail(String userEmail);

    boolean existsByBookingId(Long bookingId);
}