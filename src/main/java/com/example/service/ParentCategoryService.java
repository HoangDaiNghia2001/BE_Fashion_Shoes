package com.example.service;

import com.example.Entity.ParentCategory;
import com.example.request.ParentCategoryRequest;
import com.example.response.Response;
import com.example.response.ResponseError;

import java.util.List;
import java.util.Set;

public interface ParentCategoryService {
    ParentCategory createdParentCategory(ParentCategoryRequest parentCategoryRequest) throws ResponseError;

    ParentCategory updateParentCategory(Long id, ParentCategoryRequest parentCategoryRequest) throws ResponseError;

    Response deleteParentCategory(Long id) throws ResponseError;

    Set<ParentCategory> getAllParentCategoriesByBrandId(Long brandId) throws ResponseError;

}
