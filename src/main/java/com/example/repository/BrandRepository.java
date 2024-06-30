package com.example.repository;

import com.example.Entity.Brand;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface BrandRepository extends JpaRepository<Brand,Long> {
    Optional<Brand> findByName(String name);
}
