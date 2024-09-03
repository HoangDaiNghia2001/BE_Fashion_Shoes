package com.example.api;

import com.example.Entity.ChildCategory;
import com.example.response.ResponseData;
import com.example.exception.CustomException;
import com.example.service.implement.ChildCategoryServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController("childCategory")
@RequestMapping("/api")
public class ApiChildCategory {
    @Autowired
    private ChildCategoryServiceImpl childCategoryService;

    // CALL SUCCESS
    @GetMapping("/childCategories")
    public ResponseEntity<?> getChildCategoryByParentCategoryId(@RequestParam("parentCategoryId") Long parentCategoryId) throws CustomException {
        List<ChildCategory> childCategories = childCategoryService.getAllChildCategoriesByParentCategoryId(parentCategoryId);

        ResponseData<List<ChildCategory>> responseData = new ResponseData<>();
        responseData.setMessage("Get all child categories by parent category id success !!!");
        responseData.setStatus(HttpStatus.OK.value());
        responseData.setResults(childCategories);

        return new ResponseEntity<>(responseData, HttpStatus.OK);
    }
}
