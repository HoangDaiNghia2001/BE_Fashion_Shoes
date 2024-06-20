package com.example.repository;

import com.example.Entity.VNPayInformation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VNPayRepository extends JpaRepository<VNPayInformation,Long> {
    VNPayInformation findByOrderId(Long orderId);
}
