package com.example.mapper;

import com.example.Entity.Brand;
import com.example.request.BrandRequest;
import com.example.response.BrandResponse;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface BrandMapper {
    BrandResponse brandToBrandResponse(Brand brand);
}
