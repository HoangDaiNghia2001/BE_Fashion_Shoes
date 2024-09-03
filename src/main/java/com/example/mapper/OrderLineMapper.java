package com.example.mapper;

import com.example.Entity.OrderLine;
import com.example.request.OrderLineRequest;
import com.example.response.OrderLineResponse;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface OrderLineMapper {
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE) // các field có giá trị NULL trong request sẽ không được map
    void orderLineRequestToOrderLine(OrderLineRequest orderLineRequest, @MappingTarget OrderLine orderLine);
}
