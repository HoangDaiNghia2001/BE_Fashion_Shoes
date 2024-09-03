package com.example.service;

import com.example.Entity.ParentCategory;
import com.example.request.ParentCategoryRequest;
import com.example.response.Response;
import com.example.exception.CustomException;

import java.util.Set;

public interface ParentCategoryService {
    ParentCategory getById(Long id) throws CustomException;
    ParentCategory getByIdAndBrandId(Long id, Long brandId) throws CustomException;
    ParentCategory createdParentCategory(ParentCategoryRequest parentCategoryRequest) throws CustomException;

    ParentCategory updateParentCategory(Long id, ParentCategoryRequest parentCategoryRequest) throws CustomException;

    Response deleteParentCategory(Long id) throws CustomException;

    Set<ParentCategory> getAllParentCategoriesByBrandId(Long brandId) throws CustomException;

}
