package com.practice.controller;

import java.util.List;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.practice.entity.Service;
import com.practice.service.ServiceService;

@RestController
@RequestMapping("/api/services")
@CrossOrigin(
    origins = {
        "http://localhost:5173",
        "http://localhost:5174"
    }
)
public class ServiceController {

    private final ServiceService serviceService;

    public ServiceController(
            ServiceService serviceService) {

        this.serviceService = serviceService;
    }

    @GetMapping
    public List<Service> getAllServices() {

        return serviceService.getAllServices();
    }

    @GetMapping("/{id}")
    public Service getServiceById(
            @PathVariable Long id) {

        return serviceService.getServiceById(id);
    }

    @PostMapping
    public Service createService(
            @RequestBody Service service) {

        return serviceService.saveService(service);
    }

    @DeleteMapping("/{id}")
    public void deleteService(
            @PathVariable Long id) {

        serviceService.deleteService(id);
    }
}