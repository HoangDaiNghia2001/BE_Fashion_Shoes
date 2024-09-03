package com.example.service;

import com.example.Entity.Cart;
import com.example.request.CartRequest;
import com.example.response.CartItemResponse;
import com.example.response.CartResponse;
import com.example.response.Response;
import com.example.exception.CustomException;

import java.util.List;

public interface CartService {
    Cart getById(Long id) throws CustomException;
    CartItemResponse addToCart(CartRequest cartRequest) throws CustomException;

    CartItemResponse updateCartItem(Long id, CartRequest cartRequest) throws CustomException;

    Response deleteCartItem(Long id) throws CustomException;

    Response deleteMultiCartItem(List<Long> idCarts) throws CustomException;

    CartResponse getCartDetails() throws CustomException;

    int countCartItem() throws CustomException;
}
