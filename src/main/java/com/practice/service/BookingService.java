package com.practice.service;

import com.practice.entity.Booking;
import com.practice.entity.Service;
import com.practice.repository.BookingRepository;
import com.practice.repository.ServiceRepository;

import java.time.LocalDateTime;
import java.util.List;

@org.springframework.stereotype.Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final ServiceRepository serviceRepository;

    public BookingService(
            BookingRepository bookingRepository,
            ServiceRepository serviceRepository
    ) {
        this.bookingRepository = bookingRepository;
        this.serviceRepository = serviceRepository;
    }

    // =========================================================
    // CREATE BOOKING
    // =========================================================

    public Booking createBooking(
            Booking booking,
            String userEmail
    ) {

        Service service = serviceRepository
                .findById(booking.getServiceId())
                .orElse(null);

        if (service == null) {
            throw new RuntimeException("Service not found");
        }

        booking.setUserEmail(userEmail);

        booking.setServiceTitle(
                service.getTitle()
        );

        booking.setPrice(
                service.getPrice()
        );

        booking.setStatus("CONFIRMED");

        booking.setProviderId(null);

        booking.setCreatedAt(
                LocalDateTime.now()
        );

        return bookingRepository.save(booking);
    }

    // =========================================================
    // CUSTOMER BOOKINGS
    // =========================================================

    public List<Booking> getMyBookings(
            String userEmail
    ) {

        return bookingRepository.findByUserEmail(
                userEmail
        );
    }

    // =========================================================
    // ALL BOOKINGS - ADMIN
    // =========================================================

    public List<Booking> getAllBookings() {

        return bookingRepository.findAll();
    }

    // =========================================================
    // PROVIDER BOOKINGS
    // =========================================================

    public List<Booking> getProviderBookings(
            Long providerId
    ) {

        if (providerId == null) {
            throw new RuntimeException(
                    "Provider ID is required"
            );
        }

        return bookingRepository.findByProviderId(
                providerId
        );
    }

    // =========================================================
    // ADMIN - ASSIGN PROVIDER
    // =========================================================

    public Booking assignProvider(
            Long bookingId,
            Long providerId
    ) {

        if (providerId == null) {
            throw new RuntimeException(
                    "Provider ID is required"
            );
        }

        Booking booking = bookingRepository
                .findById(bookingId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Booking not found"
                        )
                );

        String currentStatus = booking.getStatus();

        if (currentStatus != null) {
            currentStatus = currentStatus.toUpperCase();
        }

        // Cannot assign provider to cancelled booking
        if ("CANCELLED".equals(currentStatus)) {
            throw new RuntimeException(
                    "Cannot assign provider to a cancelled booking"
            );
        }

        // Cannot assign provider after completion
        if ("COMPLETED".equals(currentStatus)) {
            throw new RuntimeException(
                    "Cannot assign provider to a completed booking"
            );
        }

        booking.setProviderId(providerId);

        booking.setStatus("ASSIGNED");

        return bookingRepository.save(booking);
    }

    // =========================================================
    // PROVIDER - UPDATE BOOKING STATUS
    // =========================================================

    public Booking updateStatus(
            Long bookingId,
            Long providerId,
            String status
    ) {

        Booking booking = bookingRepository
                .findById(bookingId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Booking not found"
                        )
                );

        // Provider must be assigned
        if (booking.getProviderId() == null) {

            throw new RuntimeException(
                    "No provider is assigned to this booking"
            );
        }

        // Make sure provider owns this booking
        if (!booking.getProviderId().equals(providerId)) {

            throw new RuntimeException(
                    "This booking is not assigned to this provider"
            );
        }

        if (status == null || status.trim().isEmpty()) {

            throw new RuntimeException(
                    "Booking status is required"
            );
        }

        status = status.toUpperCase();

        String currentStatus = booking.getStatus();

        if (currentStatus == null) {

            throw new RuntimeException(
                    "Booking has no current status"
            );
        }

        currentStatus = currentStatus.toUpperCase();

        // =====================================================
        // VALID STATUSES
        // =====================================================

        if (!isValidStatus(status)) {

            throw new RuntimeException(
                    "Invalid booking status: " + status
            );
        }

        // =====================================================
        // CANNOT CHANGE CANCELLED BOOKING
        // =====================================================

        if ("CANCELLED".equals(currentStatus)) {

            throw new RuntimeException(
                    "Cancelled bookings cannot be updated"
            );
        }

        // =====================================================
        // CANNOT CHANGE COMPLETED BOOKING
        // =====================================================

        if ("COMPLETED".equals(currentStatus)) {

            throw new RuntimeException(
                    "Completed bookings cannot be updated"
            );
        }

        // =====================================================
        // PROVIDER CANNOT CANCEL BOOKING
        // =====================================================

        if ("CANCELLED".equals(status)) {

            throw new RuntimeException(
                    "Provider cannot cancel a booking"
            );
        }

        // =====================================================
        // SAME STATUS
        // =====================================================

        if (currentStatus.equals(status)) {

            throw new RuntimeException(
                    "Booking is already " + status
            );
        }

        // =====================================================
        // VALID STATUS TRANSITIONS
        // =====================================================

        if (!isAllowedTransition(
                currentStatus,
                status
        )) {

            throw new RuntimeException(
                    "Invalid status transition from "
                            + currentStatus
                            + " to "
                            + status
            );
        }

        booking.setStatus(status);

        return bookingRepository.save(booking);
    }

    // =========================================================
    // CUSTOMER - CANCEL BOOKING
    // =========================================================

    public Booking cancelBooking(
            Long bookingId,
            String userEmail
    ) {

        Booking booking = bookingRepository
                .findById(bookingId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Booking not found"
                        )
                );

        // =====================================================
        // ONLY BOOKING OWNER CAN CANCEL
        // =====================================================

        if (booking.getUserEmail() == null ||
                !booking.getUserEmail().equals(userEmail)) {

            throw new RuntimeException(
                    "You are not allowed to cancel this booking"
            );
        }

        String currentStatus = booking.getStatus();

        if (currentStatus == null) {

            throw new RuntimeException(
                    "Booking has no current status"
            );
        }

        currentStatus = currentStatus.toUpperCase();

        // =====================================================
        // COMPLETED CANNOT BE CANCELLED
        // =====================================================

        if ("COMPLETED".equals(currentStatus)) {

            throw new RuntimeException(
                    "Completed bookings cannot be cancelled"
            );
        }

        // =====================================================
        // ALREADY CANCELLED
        // =====================================================

        if ("CANCELLED".equals(currentStatus)) {

            throw new RuntimeException(
                    "Booking is already cancelled"
            );
        }

        // =====================================================
        // SERVICE ALREADY STARTED
        // =====================================================

        if ("IN_PROGRESS".equals(currentStatus)) {

            throw new RuntimeException(
                    "Booking cannot be cancelled after service has started"
            );
        }

        // =====================================================
        // CANCEL BOOKING
        // =====================================================

        booking.setStatus("CANCELLED");

        return bookingRepository.save(booking);
    }

    // =========================================================
    // CHECK VALID STATUS
    // =========================================================

    private boolean isValidStatus(
            String status
    ) {

        return "CONFIRMED".equals(status)
                || "ASSIGNED".equals(status)
                || "ACCEPTED".equals(status)
                || "IN_PROGRESS".equals(status)
                || "COMPLETED".equals(status)
                || "CANCELLED".equals(status);
    }

    // =========================================================
    // CHECK ALLOWED STATUS TRANSITION
    // =========================================================

    private boolean isAllowedTransition(
            String currentStatus,
            String newStatus
    ) {

        if ("CONFIRMED".equals(currentStatus)
                && "ASSIGNED".equals(newStatus)) {

            return true;
        }

        if ("ASSIGNED".equals(currentStatus)
                && "ACCEPTED".equals(newStatus)) {

            return true;
        }

        if ("ACCEPTED".equals(currentStatus)
                && "IN_PROGRESS".equals(newStatus)) {

            return true;
        }

        if ("IN_PROGRESS".equals(currentStatus)
                && "COMPLETED".equals(newStatus)) {

            return true;
        }

        return false;
    }
}