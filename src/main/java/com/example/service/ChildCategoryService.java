package com.example.service;

import com.example.Entity.ChildCategory;
import com.example.exception.CustomException;
import com.example.request.ChildCategoryRequest;
import com.example.response.Response;
import com.example.response.ResponseError;

import java.util.List;

public interface ChildCategoryService {
    ChildCategory createChildCategory(ChildCategoryRequest childCategoryRequest) throws ResponseError;
    ChildCategory updateChildCategory(Long id, ChildCategoryRequest childCategoryRequest) throws ResponseError;
    Response deleteChildCategory(Long id) throws ResponseError;

    List<ChildCategory> getAllChildCategoriesByParentCategoryId(Long parentCategoryId) throws ResponseError;
}
