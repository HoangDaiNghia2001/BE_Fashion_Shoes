package com.example.api;

import com.example.Entity.ParentCategory;
import com.example.exception.CustomException;
import com.example.service.implement.ParentCategoryServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController("parentCategory")
@RequestMapping("/api")
//@CrossOrigin(origins = {"http://localhost:3000/","http://localhost:3001/","https://fashion-shoes.vercel.app/"}, allowCredentials = "true")
public class ApiParentCategory {
    @Autowired
    private ParentCategoryServiceImpl parentCategoryService;

    // CALL SUCCESS
    @GetMapping("/parentCategories")
    public ResponseEntity<?> getParentCategoryByBrandId(@RequestParam("brandId") Long brandId) throws CustomException {
        Set<ParentCategory> parentCategories = parentCategoryService.getAllParentCategoryByBrandId(brandId);
        return new ResponseEntity<>(parentCategories, HttpStatus.OK);
    }
}
