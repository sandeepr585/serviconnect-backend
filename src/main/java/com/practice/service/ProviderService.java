package com.practice.service;

import java.util.List;

import com.practice.entity.Provider;
import com.practice.repository.ProviderRepository;

@org.springframework.stereotype.Service
public class ProviderService {

    private final ProviderRepository providerRepository;

    public ProviderService(
            ProviderRepository providerRepository) {

        this.providerRepository =
                providerRepository;
    }

    public List<Provider> getAllProviders() {

        return providerRepository.findAll();
    }

    public Provider getProviderById(Long id) {

        return providerRepository
                .findById(id)
                .orElse(null);
    }

    public Provider saveProvider(
            Provider provider) {

        return providerRepository.save(provider);
    }

    public List<Provider> getProvidersByCategory(
            String category) {

        return providerRepository
                .findByServiceCategory(category);
    }

    public List<Provider> getProvidersByLocation(
            String location) {

        return providerRepository
                .findByLocation(location);
    }
}