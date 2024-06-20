package com.example.api;

import com.example.Entity.ChildCategory;
import com.example.exception.CustomException;
import com.example.service.implement.ChildCategoryServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("childCategory")
@RequestMapping("/api")
//@CrossOrigin(origins = {"http://localhost:3000/","http://localhost:3001/","https://fashion-shoes.vercel.app/"}, allowCredentials = "true")
public class ApiChildCategory {
    @Autowired
    private ChildCategoryServiceImpl childCategoryService;

    // CALL SUCCESS
    @GetMapping("/childCategories")
    public ResponseEntity<?> getChildCategoryByParentCategoryId(@RequestParam("parentCategoryId") Long parentCategoryId) throws CustomException {
        List<ChildCategory> childCategories = childCategoryService.getAllChildCategoryByParentCategoryId(parentCategoryId);

        return new ResponseEntity<>(childCategories, HttpStatus.OK);
    }
}
