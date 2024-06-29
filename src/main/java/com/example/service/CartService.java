package com.example.service;

import com.example.Entity.Cart;
import com.example.exception.CustomException;
import com.example.request.CartRequest;
import com.example.response.CartItemResponse;
import com.example.response.CartResponse;
import com.example.response.Response;

import java.util.List;

public interface CartService {
    CartItemResponse addToCart(CartRequest cartRequest) throws CustomException;

    CartItemResponse updateCartItem(CartRequest cartRequest, Long id) throws CustomException;

    String deleteCartItem(Long id) throws CustomException;

    String deleteMultiCartItem (List<Long> idCarts) throws CustomException;

    CartResponse getCartDetails() throws CustomException;

    int countCartItem() throws CustomException;
}
