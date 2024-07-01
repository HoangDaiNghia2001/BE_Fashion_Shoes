package com.example.mapper;

import com.example.Entity.ChildCategory;
import com.example.request.ChildCategoryRequest;
import com.example.response.ChildCategoryResponse;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ChildCategoryMapper {
    ChildCategoryResponse childCategoryToChildCategoryResponse(ChildCategory childCategory);
}
