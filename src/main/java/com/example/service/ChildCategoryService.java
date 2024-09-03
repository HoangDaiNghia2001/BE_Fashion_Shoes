package com.example.service;

import com.example.Entity.ChildCategory;
import com.example.request.ChildCategoryRequest;
import com.example.response.Response;
import com.example.exception.CustomException;

import java.util.List;

public interface ChildCategoryService {
    ChildCategory getById(Long id) throws CustomException;
    ChildCategory getByIdAndParentCategoryId(Long id, Long parentId) throws CustomException;

    ChildCategory createChildCategory(ChildCategoryRequest childCategoryRequest) throws CustomException;

    ChildCategory updateChildCategory(Long id, ChildCategoryRequest childCategoryRequest) throws CustomException;

    Response deleteChildCategory(Long id) throws CustomException;

    List<ChildCategory> getAllChildCategoriesByParentCategoryId(Long parentCategoryId) throws CustomException;
}
