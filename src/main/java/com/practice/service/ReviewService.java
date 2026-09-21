package com.practice.service;

import com.practice.entity.Booking;
import com.practice.entity.Review;
import com.practice.repository.BookingRepository;
import com.practice.repository.ReviewRepository;

import java.time.LocalDateTime;
import java.util.List;

@org.springframework.stereotype.Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final BookingRepository bookingRepository;

    public ReviewService(
            ReviewRepository reviewRepository,
            BookingRepository bookingRepository
    ) {
        this.reviewRepository = reviewRepository;
        this.bookingRepository = bookingRepository;
    }

    public Review createReview(
            Long bookingId,
            String userEmail,
            Integer rating,
            String comment
    ) {

        if (rating == null) {
            throw new RuntimeException(
                    "Rating is required"
            );
        }

        if (rating < 1 || rating > 5) {
            throw new RuntimeException(
                    "Rating must be between 1 and 5"
            );
        }

        Booking booking =
                bookingRepository.findById(bookingId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Booking not found"
                                )
                        );

        if (!booking.getUserEmail().equals(userEmail)) {
            throw new RuntimeException(
                    "You are not allowed to review this booking"
            );
        }

        if (!"COMPLETED".equalsIgnoreCase(
                booking.getStatus()
        )) {
            throw new RuntimeException(
                    "Only completed bookings can be reviewed"
            );
        }

        if (booking.getProviderId() == null) {
            throw new RuntimeException(
                    "This booking has no assigned provider"
            );
        }

        if (reviewRepository.existsByBookingId(bookingId)) {
            throw new RuntimeException(
                    "This booking has already been reviewed"
            );
        }

        Review review = new Review();

        review.setBookingId(bookingId);

        review.setUserEmail(userEmail);

        review.setProviderId(
                booking.getProviderId()
        );

        review.setRating(rating);

        review.setComment(
                comment == null
                        ? null
                        : comment.trim()
        );

        review.setCreatedAt(
                LocalDateTime.now()
        );

        return reviewRepository.save(review);
    }

    public List<Review> getProviderReviews(
            Long providerId
    ) {
        return reviewRepository.findByProviderId(
                providerId
        );
    }

    public List<Review> getMyReviews(
            String userEmail
    ) {
        return reviewRepository.findByUserEmail(
                userEmail
        );
    }

    public Review getBookingReview(
            Long bookingId
    ) {
        return reviewRepository
                .findByBookingId(bookingId)
                .orElse(null);
    }
}