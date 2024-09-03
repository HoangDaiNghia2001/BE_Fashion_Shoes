package com.example.api.admin;

import com.example.Entity.ChildCategory;
import com.example.request.ChildCategoryRequest;
import com.example.response.Response;
import com.example.response.ResponseData;
import com.example.exception.CustomException;
import com.example.service.implement.ChildCategoryServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController("ChildCategoryRoleAdmin")
@RequestMapping("/api/admin")
public class ApiChildCategory {
    @Autowired
    private ChildCategoryServiceImpl childCategoryService;

    // CALL SUCCESS
    @PostMapping("/childCategory")
    public ResponseEntity<?> createChildCategory(@RequestBody ChildCategoryRequest childCategoryRequest) throws CustomException {
        ChildCategory childCategory = childCategoryService.createChildCategory(childCategoryRequest);

        ResponseData<ChildCategory> responseData = new ResponseData<>();
        responseData.setResults(childCategory);
        responseData.setMessage("Child category created success !!!");
        responseData.setStatus(HttpStatus.CREATED.value());

        return new ResponseEntity<>(responseData, HttpStatus.CREATED);
    }

    // CALL SUCCESS
    @PutMapping("/childCategory")
    public ResponseEntity<?> updateChildCategory(@RequestParam("id")Long id,
                                                 @RequestBody ChildCategoryRequest childCategoryRequest) throws CustomException {
        ChildCategory childCategory = childCategoryService.updateChildCategory(id,childCategoryRequest);

        ResponseData<ChildCategory> responseData = new ResponseData<>();
        responseData.setResults(childCategory);
        responseData.setMessage("Child category updated success !!!");
        responseData.setStatus(HttpStatus.OK.value());

        return new ResponseEntity<>(responseData, HttpStatus.OK);
    }

    // CALL SUCCESS
    @DeleteMapping("/childCategory")
    public ResponseEntity<?> deleteChildCategory(@RequestParam("id")Long id) throws CustomException {
        Response response = childCategoryService.deleteChildCategory(id);
        return new ResponseEntity<>(response,HttpStatus.OK);
    }
}
