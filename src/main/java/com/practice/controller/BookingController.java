package com.practice.controller;

import com.practice.entity.Booking;
import com.practice.service.BookingService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
@CrossOrigin(origins = {
        "http://localhost:5173",
        "http://localhost:5174"
})
public class BookingController {

    private final BookingService bookingService;

    public BookingController(
            BookingService bookingService
    ) {
        this.bookingService = bookingService;
    }

    // =========================================================
    // CREATE BOOKING
    // =========================================================

    @PostMapping
    public ResponseEntity<?> createBooking(

            @RequestBody Booking booking,

            org.springframework.security.core.Authentication authentication

    ) {

        try {

            String userEmail =
                    authentication.getName();

            Booking savedBooking =
                    bookingService.createBooking(
                            booking,
                            userEmail
                    );

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(savedBooking);

        } catch (RuntimeException e) {

            return ResponseEntity
                    .badRequest()
                    .body(e.getMessage());
        }
    }

    // =========================================================
    // CUSTOMER - MY BOOKINGS
    // =========================================================

    @GetMapping("/my-bookings")
    public ResponseEntity<List<Booking>> getMyBookings(

            org.springframework.security.core.Authentication authentication

    ) {

        String userEmail =
                authentication.getName();

        return ResponseEntity.ok(
                bookingService.getMyBookings(
                        userEmail
                )
        );
    }

    // =========================================================
    // ADMIN - ALL BOOKINGS
    // =========================================================

    @GetMapping
    public ResponseEntity<List<Booking>> getAllBookings() {

        return ResponseEntity.ok(
                bookingService.getAllBookings()
        );
    }

    // =========================================================
    // PROVIDER - ASSIGNED BOOKINGS
    // =========================================================

    @GetMapping("/provider/{providerId}")
    public ResponseEntity<List<Booking>> getProviderBookings(

            @PathVariable Long providerId

    ) {

        return ResponseEntity.ok(
                bookingService.getProviderBookings(
                        providerId
                )
        );
    }

    // =========================================================
    // ADMIN - ASSIGN PROVIDER
    // =========================================================

    @PutMapping("/{bookingId}/assign/{providerId}")
    public ResponseEntity<?> assignProvider(

            @PathVariable Long bookingId,

            @PathVariable Long providerId

    ) {

        try {

            Booking booking =
                    bookingService.assignProvider(
                            bookingId,
                            providerId
                    );

            return ResponseEntity.ok(booking);

        } catch (RuntimeException e) {

            return ResponseEntity
                    .badRequest()
                    .body(e.getMessage());
        }
    }

    // =========================================================
    // PROVIDER - UPDATE BOOKING STATUS
    // =========================================================

    @PutMapping("/{bookingId}/status")
    public ResponseEntity<?> updateStatus(

            @PathVariable Long bookingId,

            @RequestParam Long providerId,

            @RequestParam String status

    ) {

        try {

            Booking booking =
                    bookingService.updateStatus(
                            bookingId,
                            providerId,
                            status
                    );

            return ResponseEntity.ok(booking);

        } catch (RuntimeException e) {

            return ResponseEntity
                    .badRequest()
                    .body(e.getMessage());
        }
    }

    // =========================================================
    // CUSTOMER - CANCEL BOOKING
    // =========================================================

    @PutMapping("/{bookingId}/cancel")
    public ResponseEntity<?> cancelBooking(

            @PathVariable Long bookingId,

            org.springframework.security.core.Authentication authentication

    ) {

        try {

            String userEmail =
                    authentication.getName();

            Booking booking =
                    bookingService.cancelBooking(
                            bookingId,
                            userEmail
                    );

            return ResponseEntity.ok(booking);

        } catch (RuntimeException e) {

            return ResponseEntity
                    .badRequest()
                    .body(e.getMessage());
        }
    }
}