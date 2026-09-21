package com.practice.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.practice.entity.Provider;

public interface ProviderRepository
        extends JpaRepository<Provider, Long> {

    List<Provider> findByServiceCategory(
            String serviceCategory
    );

    List<Provider> findByLocation(
            String location
    );

    List<Provider> findByStatus(
            String status
    );
}