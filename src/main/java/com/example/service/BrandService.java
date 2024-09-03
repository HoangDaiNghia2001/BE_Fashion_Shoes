package com.example.service;

import com.example.Entity.Brand;
import com.example.request.BrandRequest;
import com.example.response.BrandResponse;
import com.example.response.Response;
import com.example.exception.CustomException;

import java.util.List;

public interface BrandService {
    Brand getById(Long id) throws CustomException;
    List<BrandResponse> getAllBrandsDetailByAdmin();

    Brand createBrand(BrandRequest brand) throws CustomException;

    Brand updateBrand(Long id, BrandRequest brand) throws CustomException;

    Response deleteBrand(Long id) throws CustomException;

    List<Brand> getAllBrands();

}
