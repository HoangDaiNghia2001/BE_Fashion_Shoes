package com.example.mapper;

import com.example.Entity.ParentCategory;
import com.example.request.ParentCategoryRequest;
import com.example.response.ParentCategoryResponse;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ParentCategoryMapper {
    ParentCategoryResponse parentCategoryToParentCategoryResponse(ParentCategory parentCategory);
}
