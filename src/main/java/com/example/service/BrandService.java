package com.example.service;

import com.example.Entity.Brand;
import com.example.request.BrandRequest;
import com.example.response.BrandResponse;
import com.example.response.Response;
import com.example.response.ResponseError;

import java.util.List;

public interface BrandService {
    List<BrandResponse> getAllBrandsDetailByAdmin();

    Brand createBrand(BrandRequest brand) throws ResponseError;

    Brand updateBrand(Long id, BrandRequest brand) throws ResponseError;

    Response deleteBrand(Long id) throws ResponseError;

    List<Brand> getAllBrands();

}
