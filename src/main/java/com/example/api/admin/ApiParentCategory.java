package com.example.api.admin;

import com.example.Entity.ParentCategory;
import com.example.exception.CustomException;
import com.example.request.ParentCategoryRequest;
import com.example.response.Response;
import com.example.response.ResponseData;
import com.example.response.ResponseError;
import com.example.service.implement.ParentCategoryServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("parentCategoryRoleAdmin")
@RequestMapping("/api/admin")
public class ApiParentCategory {
    @Autowired
    private ParentCategoryServiceImpl parentCategoryService;

    // CALL SUCCESS
    @PostMapping("/parentCategory")
    public ResponseEntity<?> createParentCategory(@RequestBody ParentCategoryRequest parentCategoryRequest) throws ResponseError {
        ParentCategory parentCategory = parentCategoryService.createdParentCategory(parentCategoryRequest);

        ResponseData<ParentCategory> responseData = new ResponseData<>();
        responseData.setResults(parentCategory);
        responseData.setMessage("Parent category created success !!!");
        responseData.setStatus(HttpStatus.CREATED.value());

        return new ResponseEntity<>(responseData, HttpStatus.CREATED);
    }

    // CALL SUCCESS
    @PutMapping("parentCategory")
    public ResponseEntity<?> updateParentCategory(@RequestParam("id") Long id,
                                                  @RequestBody ParentCategoryRequest parentCategoryRequest) throws ResponseError {
        ParentCategory parentCategory = parentCategoryService.updateParentCategory(id, parentCategoryRequest);

        ResponseData<ParentCategory> responseData = new ResponseData<>();
        responseData.setResults(parentCategory);
        responseData.setMessage("Parent category updated success !!!");
        responseData.setStatus(HttpStatus.OK.value());

        return new ResponseEntity<>(responseData, HttpStatus.OK);
    }

    // CALL SUCCESS
    @DeleteMapping("/parentCategory")
    public ResponseEntity<?> deleteParentCategory(@RequestParam("id") Long id) throws ResponseError {
        Response response = parentCategoryService.deleteParentCategory(id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
