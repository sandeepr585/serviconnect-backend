package com.practice.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.practice.entity.Service;

public interface ServiceRepository extends JpaRepository<Service, Long> {

}