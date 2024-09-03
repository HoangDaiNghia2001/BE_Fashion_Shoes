package com.example.mapper;

import com.example.Entity.Cart;
import com.example.request.CartRequest;
import com.example.response.CartItemResponse;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface CartMapper {
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE) // các field có giá trị NULL trong request sẽ không được map
    void cartRequestToCart(CartRequest cartRequest, @MappingTarget Cart cart);

    CartItemResponse cartToCartItemResponse(Cart cart);
}
