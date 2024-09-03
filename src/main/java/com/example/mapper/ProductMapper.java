package com.example.mapper;

import com.example.Entity.Product;
import com.example.request.ProductRequest;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE) // các field có giá trị NULL trong request sẽ không được map
    @Mapping(target = "sizes", ignore = true)
    void productRequestToProduct(ProductRequest productRequest, @MappingTarget Product product);
}
