package com.example.service.implement;

import com.example.Entity.Brand;
import com.example.Entity.User;
import com.example.config.JwtProvider;
import com.example.constant.CookieConstant;
import com.example.exception.CustomException;
import com.example.repository.BrandRepository;
import com.example.request.BrandRequest;
import com.example.response.BrandResponse;
import com.example.response.ChildCategoryResponse;
import com.example.response.ParentCategoryResponse;
import com.example.service.BrandService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
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
    private HttpServletRequest request;
    @Autowired
    private JwtProvider jwtProvider;
    @Autowired
    private UserServiceImpl userService;

    @Override
    public List<BrandResponse> getAllBrandDetailByAdmin() {
        List<BrandResponse> brandResponseList = new ArrayList<>();

        List<Brand> brandList = brandRepository.findAll();
        brandList.forEach(brand -> {
            BrandResponse brandResponse = new BrandResponse();

            brandResponse.setId(brand.getId());
            brandResponse.setName(brand.getName());
            // add list parent category to brandResponse
            List<ParentCategoryResponse> parentCategoryResponseList = new ArrayList<>();

            brand.getParentCategories().forEach(parentCategory -> {
                ParentCategoryResponse parentCategoryResponse = new ParentCategoryResponse();
                parentCategoryResponse.setId(parentCategory.getId());
                parentCategoryResponse.setBrandId(brand.getId());
                parentCategoryResponse.setName(parentCategory.getName());
                // add list child category to parent categoryResponse
                List<ChildCategoryResponse> childCategoryResponseList = new ArrayList<>();

                parentCategory.getChildCategories().forEach(childCategory -> {
                    ChildCategoryResponse childCategoryResponse = new ChildCategoryResponse();
                    childCategoryResponse.setId(childCategory.getId());
                    childCategoryResponse.setParentCategoryId(parentCategory.getId());
                    childCategoryResponse.setName(childCategory.getName());
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
    public List<Brand> getAllBrand() {
        return brandRepository.findAll();
    }

    @Override
    @Transactional
    public Brand createBrand(BrandRequest brandRequest) throws CustomException {
        brandRequest.setName(brandRequest.getName().toUpperCase());

        Brand check = brandRepository.findByName(brandRequest.getName());

        if (check == null) {
            String token = jwtProvider.getTokenFromCookie(request, CookieConstant.JWT_COOKIE_ADMIN);
            User admin = userService.findUserProfileByJwt(token);

            Brand brand = new Brand();
            brand.setName(brandRequest.getName());
            brand.setCreatedBy(admin.getEmail());

            return brandRepository.save(brand);
        } else {
            throw new CustomException("Brand is already exist with name: " + brandRequest.getName());
        }
    }

    @Override
    @Transactional
    public Brand updateBrand(Long id, BrandRequest brandRequest) throws CustomException {
        Optional<Brand> oldBrand = brandRepository.findById(id);

        brandRequest.setName(brandRequest.getName().toUpperCase());
        Brand check = brandRepository.findByName(brandRequest.getName());

        if (oldBrand.isPresent()) {
            if (check == null || check.getName().equals(oldBrand.get().getName())) {
                String token = jwtProvider.getTokenFromCookie(request, CookieConstant.JWT_COOKIE_ADMIN);
                User admin = userService.findUserProfileByJwt(token);

                oldBrand.get().setUpdateBy(admin.getEmail());
                oldBrand.get().setName(brandRequest.getName());

                return brandRepository.save(oldBrand.get());
            } else {
                throw new CustomException("The brand with name " + check.getName() + " is already exist !!!");
            }
        } else {
            throw new CustomException("Brand not found with id: " + id);
        }
    }

    @Override
    @Transactional
    public void deleteBrand(Long id) throws CustomException {
        Optional<Brand> brand = brandRepository.findById(id);
        if (brand.isPresent()) {
            brandRepository.delete(brand.get());
        } else {
            throw new CustomException("Brand not found with id: " + id);
        }
    }

    @Override
    public Brand getBrandInformation(Long id) throws CustomException {
        Optional<Brand> brand = brandRepository.findById(id);
        return brand.orElseThrow(() -> new CustomException("Brand not found with id: " + id));
    }
}
