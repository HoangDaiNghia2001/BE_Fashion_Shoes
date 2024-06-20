package com.example.api.admin;

import com.example.Entity.ParentCategory;
import com.example.exception.CustomException;
import com.example.request.ParentCategoryRequest;
import com.example.response.Response;
import com.example.response.ResponseData;
import com.example.service.implement.ParentCategoryServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("parentCategoryRoleAdmin")
@RequestMapping("/api/admin")
//@CrossOrigin(origins = {"http://localhost:3000/","http://localhost:3001/","https://fashion-shoes.vercel.app/"}, allowCredentials = "true")
public class ApiParentCategory {
    @Autowired
    private ParentCategoryServiceImpl parentCategoryService;

    // CALL SUCCESS
    @PostMapping("/parentCategory")
    public ResponseEntity<?> createParentCategory(@RequestBody ParentCategoryRequest parentCategoryRequest) throws CustomException {
        ParentCategory parentCategory = parentCategoryService.createdParentCategory(parentCategoryRequest);

        ResponseData<ParentCategory> responseData = new ResponseData<>();
        responseData.setResults(parentCategory);
        responseData.setMessage("Parent category created success !!!");
        responseData.setSuccess(true);

        return new ResponseEntity<>(responseData, HttpStatus.OK);
    }

    // CALL SUCCESS
    @PutMapping("parentCategory")
    public ResponseEntity<?> updateParentCategory(@RequestBody ParentCategoryRequest parentCategoryRequest, @RequestParam("id") Long id) throws CustomException {
        ParentCategory parentCategory = parentCategoryService.updateParentCategory(id, parentCategoryRequest);

        ResponseData<ParentCategory> responseData = new ResponseData<>();
        responseData.setResults(parentCategory);
        responseData.setMessage("Parent category updated success !!!");
        responseData.setSuccess(true);

        return new ResponseEntity<>(responseData, HttpStatus.OK);
    }

    // CALL SUCCESS
    @DeleteMapping("/parentCategory")
    public ResponseEntity<?> deleteParentCategory(@RequestParam("id") Long id) throws CustomException {
        parentCategoryService.deleteParentCategory(id);

        Response response = new Response();
        response.setMessage("Delete success !!!");
        response.setSuccess(true);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/parentCategory")
    public ResponseEntity<?> getAllParentCategory(@RequestParam("pageIndex") int pageIndex, @RequestParam("pageSize") int pageSize) {
        List<ParentCategory> parentCategories = parentCategoryService.getAllParentCategory(pageIndex, pageSize);

        return new ResponseEntity<>(parentCategories, HttpStatus.OK);
    }
}
