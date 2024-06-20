package com.example.service;

import com.example.Entity.ChildCategory;
import com.example.exception.CustomException;
import com.example.request.ChildCategoryRequest;

import java.util.List;

public interface ChildCategoryService {
    ChildCategory createChildCategory(ChildCategoryRequest childCategoryRequest) throws CustomException;
    ChildCategory updateChildCategory(Long id, ChildCategoryRequest childCategoryRequest) throws CustomException;
    void deleteChildCategory(Long id) throws CustomException;
    List<ChildCategory> getAllChildCategory(int pageIndex, int pageSize);

    List<ChildCategory> getAllChildCategoryByParentCategoryId(Long parentCategoryId) throws CustomException;
}
