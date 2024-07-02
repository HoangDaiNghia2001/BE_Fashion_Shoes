package com.example.mapper;

import com.example.Entity.Product;
import com.example.request.ProductRequest;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    void productRequestToProduct(ProductRequest productRequest, @MappingTarget Product product);
}
