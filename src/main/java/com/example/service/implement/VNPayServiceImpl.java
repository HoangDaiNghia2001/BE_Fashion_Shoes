package com.example.service.implement;

import com.example.Entity.Order;
import com.example.Entity.VNPayInformation;
import com.example.repository.VNPayRepository;
import com.example.exception.CustomException;
import com.example.response.VNPayResponse;
import com.example.service.OrderService;
import com.example.service.VNPayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class VNPayServiceImpl implements VNPayService {

    @Autowired
    private OrderService orderService;

    @Autowired
    private VNPayRepository vnPayRepository;

    @Override
    public void createVNPayOfOrder(VNPayResponse vnPayResponse, Long orderId) throws CustomException {

        Order order = orderService.getById(orderId);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        LocalDateTime orderDate = LocalDateTime.parse(vnPayResponse.getVnp_PayDate(), formatter);

        VNPayInformation vnPayInformation = new VNPayInformation();

        vnPayInformation.setOrder(order);
        vnPayInformation.setVnp_PayDate(orderDate);
        vnPayInformation.setVnp_Amount(Long.parseLong(vnPayResponse.getVnp_Amount()) / 100);
        vnPayInformation.setVnp_OrderInfo(vnPayResponse.getVnp_OrderInfo());
        vnPayInformation.setVnp_BankCode(vnPayResponse.getVnp_BankCode());
        vnPayInformation.setVnp_ResponseCode(vnPayResponse.getVnp_ResponseCode());
        vnPayInformation.setVnp_TransactionNo(vnPayResponse.getVnp_TransactionNo());

        vnPayRepository.save(vnPayInformation);
    }

    @Override
    public VNPayInformation getVNPayInformationByOrderId(Long orderId) {
        return vnPayRepository.findByOrderId(orderId);
    }
}
