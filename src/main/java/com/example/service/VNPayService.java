package com.example.service;

import com.example.Entity.VNPayInformation;
import com.example.exception.CustomException;
import com.example.response.VNPayResponse;

public interface VNPayService {
    void createVNPayOfOrder(VNPayResponse vnPayResponse, Long orderId) throws CustomException;

    VNPayInformation getVNPayInformationByOrderId(Long orderId);
}
