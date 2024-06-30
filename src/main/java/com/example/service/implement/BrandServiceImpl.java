package com.example.service.implement;

import com.example.Entity.Brand;
import com.example.mapper.BrandMapper;
import com.example.mapper.ChildCategoryMapper;
import com.example.mapper.ParentCategoryMapper;
import com.example.repository.BrandRepository;
import com.example.request.BrandRequest;
import com.example.response.*;
import com.example.service.BrandService;
import com.example.util.MethodUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class BrandServiceImpl implements BrandService {
    @Autowired
    private BrandRepository brandRepository;
    @Autowired
    private MethodUtils methodUtils;
    @Autowired
    private BrandMapper brandMapper;
    @Autowired
    private ParentCategoryMapper parentCategoryMapper;
    @Autowired
    private ChildCategoryMapper childCategoryMapper;

    @Override
    public List<BrandResponse> getAllBrandsDetailByAdmin() {
        List<BrandResponse> brandResponseList = new ArrayList<>();

        List<Brand> brandList = brandRepository.findAll();
        brandList.forEach(brand -> {
            BrandResponse brandResponse = brandMapper.brandToBrandResponse(brand);

            // add list parent category to brandResponse
            List<ParentCategoryResponse> parentCategoryResponseList = new ArrayList<>();

            brand.getParentCategories().forEach(parentCategory -> {
                ParentCategoryResponse parentCategoryResponse = parentCategoryMapper.parentCategoryToParentCategoryResponse(parentCategory);
                parentCategoryResponse.setBrandId(brand.getId());
                // add list child category to parent category Response
                List<ChildCategoryResponse> childCategoryResponseList = new ArrayList<>();

                parentCategory.getChildCategories().forEach(childCategory -> {
                    ChildCategoryResponse childCategoryResponse = childCategoryMapper.childCategoryToChildCategoryResponse(childCategory);
                    childCategoryResponse.setParentCategoryId(parentCategory.getId());
                    childCategoryResponseList.add(childCategoryResponse);
                });
                parentCategoryResponse.setChildCategoryResponseList(childCategoryResponseList);
                parentCategoryResponseList.add(parentCategoryResponse);
            });
            brandResponse.setParentCategoryResponseList(parentCategoryResponseList);
            brandResponseList.add(brandResponse);
        });

        return brandResponseList;
    }

    @Override
    public List<Brand> getAllBrands() {
        return brandRepository.findAll();
    }

    @Override
    @Transactional
    public Brand createBrand(BrandRequest brandRequest) throws ResponseError {
        brandRequest.setName(brandRequest.getName().toUpperCase());

        Optional<Brand> brandExist = brandRepository.findByName(brandRequest.getName());

        if (brandExist.isPresent()) {
            throw new ResponseError(
                    "Brand is already exist with name: " + brandExist.get().getName(),
                    HttpStatus.CONFLICT.value());
        }
        String emailAdmin = methodUtils.getEmailFromTokenOfAdmin();

        Brand brand = new Brand();
        brand.setName(brandRequest.getName());
        brand.setCreatedBy(emailAdmin);

        return brandRepository.save(brand);

    }

    @Override
    @Transactional
    public Brand updateBrand(Long id, BrandRequest brandRequest) throws ResponseError {
        Brand oldBrand = brandRepository.findById(id)
                .orElseThrow(() -> new ResponseError(
                        "Brand not found with id: " + id,
                        HttpStatus.NOT_FOUND.value()
                ));

        brandRequest.setName(brandRequest.getName().toUpperCase());

        Optional<Brand> brandExist = brandRepository.findByName(brandRequest.getName());

        if (!brandExist.isPresent() || brandExist.get().getName().equals(oldBrand.getName())) {
            String emailAdmin = methodUtils.getEmailFromTokenOfAdmin();

            oldBrand.setUpdateBy(emailAdmin);
            oldBrand.setName(brandRequest.getName());

            return brandRepository.save(oldBrand);
        } else {
            throw new ResponseError("The brand with name " + brandExist.get().getName() + " is already exist !!!",
                    HttpStatus.CONFLICT.value());
        }
    }

    @Override
    @Transactional
    public Response deleteBrand(Long id) throws ResponseError {
        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new ResponseError(
                        "Brand not found with id: " + id,
                        HttpStatus.NOT_FOUND.value()
                ));
        brandRepository.delete(brand);
        Response response = new Response();
        response.setMessage("Delete brand success !!!");
        response.setStatus(HttpStatus.OK.value());

        return response;
    }

}
