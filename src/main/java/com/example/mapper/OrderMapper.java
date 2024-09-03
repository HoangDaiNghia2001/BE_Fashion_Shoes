package com.example.mapper;

import com.example.Entity.Order;
import com.example.request.OrderRequest;
import com.example.response.OrderResponse;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface OrderMapper {
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE) // các field có giá trị NULL trong request sẽ không được map
    @Mapping(target = "orderLines", ignore = true)
    void orderRequestToOrder(OrderRequest orderRequest, @MappingTarget Order order);

    @Mapping(target = "orderLines", ignore = true)
    OrderResponse orderToOrderResponse(Order order);
}
