package com.practice.controller;

import com.practice.entity.Review;
import com.practice.service.ReviewService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
@CrossOrigin(origins = {
        "http://localhost:5173",
        "http://localhost:5174"
})
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(
            ReviewService reviewService
    ) {
        this.reviewService = reviewService;
    }

    @PostMapping("/booking/{bookingId}")
    public ResponseEntity<?> createReview(
            @PathVariable Long bookingId,
            @RequestBody ReviewRequest request,
            org.springframework.security.core.Authentication authentication
    ) {

        try {

            String userEmail =
                    authentication.getName();

            Review review =
                    reviewService.createReview(
                            bookingId,
                            userEmail,
                            request.getRating(),
                            request.getComment()
                    );

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(review);

        } catch (RuntimeException e) {

            return ResponseEntity
                    .badRequest()
                    .body(e.getMessage());
        }
    }

    @GetMapping("/provider/{providerId}")
    public ResponseEntity<List<Review>> getProviderReviews(
            @PathVariable Long providerId
    ) {

        return ResponseEntity.ok(
                reviewService.getProviderReviews(
                        providerId
                )
        );
    }

    @GetMapping("/my-reviews")
    public ResponseEntity<List<Review>> getMyReviews(
            org.springframework.security.core.Authentication authentication
    ) {

        String userEmail =
                authentication.getName();

        return ResponseEntity.ok(
                reviewService.getMyReviews(
                        userEmail
                )
        );
    }

    @GetMapping("/booking/{bookingId}")
    public ResponseEntity<?> getBookingReview(
            @PathVariable Long bookingId
    ) {

        Review review =
                reviewService.getBookingReview(
                        bookingId
                );

        if (review == null) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("No review found");
        }

        return ResponseEntity.ok(review);
    }

    public static class ReviewRequest {

        private Integer rating;

        private String comment;

        public Integer getRating() {
            return rating;
        }

        public void setRating(Integer rating) {
            this.rating = rating;
        }

        public String getComment() {
            return comment;
        }

        public void setComment(String comment) {
            this.comment = comment;
        }
    }
}