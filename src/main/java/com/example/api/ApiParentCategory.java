package com.example.api;

import com.example.Entity.ParentCategory;
import com.example.exception.CustomException;
import com.example.response.ResponseData;
import com.example.response.ResponseError;
import com.example.service.implement.ParentCategoryServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController("parentCategory")
@RequestMapping("/api")
public class ApiParentCategory {
    @Autowired
    private ParentCategoryServiceImpl parentCategoryService;

    // CALL SUCCESS
    @GetMapping("/parentCategories")
    public ResponseEntity<?> getParentCategoryByBrandId(@RequestParam("brandId") Long brandId) throws ResponseError {
        Set<ParentCategory> parentCategories = parentCategoryService.getAllParentCategoriesByBrandId(brandId);

        ResponseData<Set<ParentCategory>> responseData = new ResponseData<>();
        responseData.setMessage("Get all parent categories by brand id success !!!");
        responseData.setStatus(HttpStatus.OK.value());
        responseData.setResults(parentCategories);

        return new ResponseEntity<>(responseData, HttpStatus.OK);
    }
}
