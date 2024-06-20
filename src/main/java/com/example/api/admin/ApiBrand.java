package com.example.api.admin;

import com.example.Entity.Brand;
import com.example.exception.CustomException;
import com.example.request.BrandRequest;
import com.example.response.BrandResponse;
import com.example.response.Response;
import com.example.response.ResponseData;
import com.example.service.implement.BrandServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("brandOfAdmin")
@RequestMapping("/api/admin")
//@CrossOrigin(origins = {"http://localhost:3000/","http://localhost:3001/","https://fashion-shoes.vercel.app/"}, allowCredentials = "true")
public class ApiBrand {
    @Autowired
    private BrandServiceImpl brandService;

    // CALL SUCCESS
    @GetMapping("/brands/detail")
    public ResponseEntity<?> getAllBrandsDetail(){
        List<BrandResponse> brandResponseList = brandService.getAllBrandDetailByAdmin();

        ResponseData<List<BrandResponse>> responseData = new ResponseData<>();
        responseData.setResults(brandResponseList);
        responseData.setMessage("Get all brand detail success !!!");
        responseData.setSuccess(true);

        return new ResponseEntity<>(responseData, HttpStatus.OK);
    }

    // CALL SUCCESS
    @PostMapping("/brand")
    public ResponseEntity<?> createBrand(@RequestBody BrandRequest brandRequest) throws CustomException {
        Brand brand = brandService.createBrand(brandRequest);

        ResponseData<Brand> responseData = new ResponseData<>();
        responseData.setMessage("Brand created success !!!");
        responseData.setSuccess(true);
        responseData.setResults(brand);

        return new ResponseEntity<>(responseData,HttpStatus.OK);
    }

    // CALL SUCCESS
    @PutMapping("/brand")
    public ResponseEntity<?> updateBrand(@RequestBody BrandRequest brandRequest,@RequestParam("id")Long id) throws CustomException {
        Brand brand = brandService.updateBrand(id,brandRequest);

        ResponseData<Brand> responseData = new ResponseData<>();
        responseData.setMessage("This brand updated success !!!");
        responseData.setSuccess(true);
        responseData.setResults(brand);

        return new ResponseEntity<>(responseData,HttpStatus.OK);
    }

    // CALL SUCCESS
    @DeleteMapping("/brand")
    public ResponseEntity<?> deleteBrand(@RequestParam("id")Long id) throws CustomException {
        brandService.deleteBrand(id);

        Response response = new Response();
        response.setMessage("Delete success this brand !!!");
        response.setSuccess(true);

        return new ResponseEntity<>(response,HttpStatus.OK);
    }

    @GetMapping("/brand")
    public ResponseEntity<?> getBrandInformation(@RequestParam("id")Long id) throws CustomException {
        Brand brand = brandService.getBrandInformation(id);
        return new ResponseEntity<>(brand, HttpStatus.OK);
    }




}
