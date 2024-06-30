package com.example.api.admin;

import com.example.Entity.Brand;
import com.example.exception.CustomException;
import com.example.request.BrandRequest;
import com.example.response.BrandResponse;
import com.example.response.Response;
import com.example.response.ResponseData;
import com.example.response.ResponseError;
import com.example.service.implement.BrandServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("brandOfAdmin")
@RequestMapping("/api/admin")
public class ApiBrand {
    @Autowired
    private BrandServiceImpl brandService;

    // CALL SUCCESS
    @GetMapping("/brands/detail")
    public ResponseEntity<?> getAllBrandsByAdmin() {
        List<BrandResponse> brandResponseList = brandService.getAllBrandsDetailByAdmin();

        ResponseData<List<BrandResponse>> responseData = new ResponseData<>();
        responseData.setResults(brandResponseList);
        responseData.setMessage("Get all brands detail success !!!");
        responseData.setStatus(HttpStatus.OK.value());

        return new ResponseEntity<>(responseData, HttpStatus.OK);
    }

    // CALL SUCCESS
    @PostMapping("/brand")
    public ResponseEntity<?> createBrand(@RequestBody BrandRequest brandRequest) throws ResponseError {
        Brand brand = brandService.createBrand(brandRequest);

        ResponseData<Brand> responseData = new ResponseData<>();
        responseData.setMessage("Brand created success !!!");
        responseData.setStatus(HttpStatus.CREATED.value());
        responseData.setResults(brand);

        return new ResponseEntity<>(responseData, HttpStatus.CREATED);
    }

    // CALL SUCCESS
    @PutMapping("/brand")
    public ResponseEntity<?> updateBrand(@RequestParam("id") Long id,
                                         @RequestBody BrandRequest brandRequest) throws ResponseError {
        Brand brand = brandService.updateBrand(id, brandRequest);

        ResponseData<Brand> responseData = new ResponseData<>();
        responseData.setMessage("This brand updated success !!!");
        responseData.setStatus(HttpStatus.OK.value());
        responseData.setResults(brand);

        return new ResponseEntity<>(responseData, HttpStatus.OK);
    }

    // CALL SUCCESS
    @DeleteMapping("/brand")
    public ResponseEntity<?> deleteBrand(@RequestParam("id") Long id) throws ResponseError {
        Response response = brandService.deleteBrand(id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
