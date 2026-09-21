package com.practice.controller;

import java.util.List;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.practice.entity.Provider;
import com.practice.service.ProviderService;

@RestController
@RequestMapping("/api/providers")
@CrossOrigin(
    origins = {
        "http://localhost:5173",
        "http://localhost:5174"
    }
)
public class ProviderController {

    private final ProviderService providerService;

    public ProviderController(
            ProviderService providerService) {

        this.providerService =
                providerService;
    }

    @GetMapping
    public List<Provider> getAllProviders() {

        return providerService
                .getAllProviders();
    }

    @GetMapping("/{id}")
    public Provider getProviderById(
            @PathVariable Long id) {

        return providerService
                .getProviderById(id);
    }

    @GetMapping("/category")
    public List<Provider> getProvidersByCategory(
            @RequestParam String value) {

        return providerService
                .getProvidersByCategory(value);
    }

    @GetMapping("/location")
    public List<Provider> getProvidersByLocation(
            @RequestParam String value) {

        return providerService
                .getProvidersByLocation(value);
    }

    @PostMapping
    public Provider createProvider(
            @RequestBody Provider provider) {

        return providerService
                .saveProvider(provider);
    }
}