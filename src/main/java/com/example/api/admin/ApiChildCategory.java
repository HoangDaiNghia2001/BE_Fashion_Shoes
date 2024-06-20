package com.example.api.admin;

import com.example.Entity.ChildCategory;
import com.example.exception.CustomException;
import com.example.request.ChildCategoryRequest;
import com.example.response.Response;
import com.example.response.ResponseData;
import com.example.service.implement.ChildCategoryServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("ChildCategoryRoleAdmin")
@RequestMapping("/api/admin")
//@CrossOrigin(origins = {"http://localhost:3000/","http://localhost:3001/","https://fashion-shoes.vercel.app/"}, allowCredentials = "true")
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
        responseData.setSuccess(true);

        return new ResponseEntity<>(responseData, HttpStatus.OK);
    }

    // CALL SUCCESS
    @PutMapping("/childCategory")
    public ResponseEntity<?> updateChildCategory(@RequestBody ChildCategoryRequest childCategoryRequest, @RequestParam("id")Long id) throws CustomException {
        ChildCategory childCategory = childCategoryService.updateChildCategory(id,childCategoryRequest);

        ResponseData<ChildCategory> responseData = new ResponseData<>();
        responseData.setResults(childCategory);
        responseData.setMessage("Child category updated success !!!");
        responseData.setSuccess(true);

        return new ResponseEntity<>(responseData, HttpStatus.OK);
    }

    // CALL SUCCESS
    @DeleteMapping("/childCategory")
    public ResponseEntity<?> deleteChildCategory(@RequestParam("id")Long id) throws CustomException {
        childCategoryService.deleteChildCategory(id);

        Response response = new Response();
        response.setMessage("Delete success !!!");
        response.setSuccess(true);

        return new ResponseEntity<>(response,HttpStatus.OK);
    }

    @GetMapping("/childCategory")
    public ResponseEntity<?> getAllChildCategory(@RequestParam("pageIndex") int pageIndex, @RequestParam("pageSize")int pageSize){
        List<ChildCategory> childCategories = childCategoryService.getAllChildCategory(pageIndex, pageSize);

        return new ResponseEntity<>(childCategories, HttpStatus.OK);
    }


}
