package com.example.api;

import com.example.Entity.Brand;
import com.example.service.implement.BrandServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("brands")
@RequestMapping("/api")
//@CrossOrigin(origins = {"http://localhost:3000/","http://localhost:3001/","https://fashion-shoes.vercel.app/"}, allowCredentials = "true")
public class ApiBrand {
    @Autowired
    private BrandServiceImpl brandService;

    // CALL SUCCESS
    @GetMapping("/brands")
    public ResponseEntity<?> getBrand(){
        List<Brand> brands= brandService.getAllBrand();
        return new ResponseEntity<>(brands, HttpStatus.OK);
    }
}
