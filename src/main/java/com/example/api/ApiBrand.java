package com.example.api;

import com.example.Entity.Brand;
import com.example.response.ResponseData;
import com.example.service.implement.BrandServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("brands")
@RequestMapping("/api")
public class ApiBrand {
    @Autowired
    private BrandServiceImpl brandService;

    // CALL SUCCESS
    @GetMapping("/brands")
    public ResponseEntity<?> getAllBrands(){
        List<Brand> brands= brandService.getAllBrands();

        ResponseData<List<Brand>> responseData = new ResponseData<>();
        responseData.setMessage("Get all brands success !!!");
        responseData.setStatus(HttpStatus.OK.value());
        responseData.setResults(brands);

        return new ResponseEntity<>(responseData, HttpStatus.OK);
    }
}
