package com.example.service.implement;

import com.example.Entity.Brand;
import com.example.Entity.ParentCategory;
import com.example.Entity.User;
import com.example.config.JwtProvider;
import com.example.constant.CookieConstant;
import com.example.exception.CustomException;
import com.example.mapper.ParentCategoryMapper;
import com.example.repository.BrandRepository;
import com.example.repository.ParentCategoryRepository;
import com.example.request.ParentCategoryRequest;
import com.example.response.Response;
import com.example.response.ResponseError;
import com.example.service.ParentCategoryService;
import com.example.util.MethodUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Set;

@Service
public class ParentCategoryServiceImpl implements ParentCategoryService {
    @Autowired
    private BrandRepository brandRepository;
    @Autowired
    private ParentCategoryRepository parentCategoryRepository;
    @Autowired
    private MethodUtils methodUtils;

    @Override
    @Transactional
    public ParentCategory createdParentCategory(ParentCategoryRequest parentCategoryRequest) throws ResponseError {
        Brand brand = brandRepository.findById(parentCategoryRequest.getBrandId())
                .orElseThrow(() -> new ResponseError(
                        "Brand not found !!!",
                        HttpStatus.NOT_FOUND.value()
                ));

        parentCategoryRequest.setName(parentCategoryRequest.getName().toUpperCase());

        Optional<ParentCategory> parentCategoryExist = parentCategoryRepository.findByNameAndBrandId(parentCategoryRequest.getName(), brand.getId());

        if (parentCategoryExist.isPresent()) {
            throw new ResponseError("Parent category with name " + parentCategoryRequest.getName() + " of brand " + brand.getName() + " already exist !!!",
                    HttpStatus.CONFLICT.value());
        }
        String emailAdmin = methodUtils.getEmailFromTokenOfAdmin();

        ParentCategory parentCategory = new ParentCategory();

        parentCategory.setName(parentCategoryRequest.getName());
        parentCategory.setCreatedBy(emailAdmin);
        parentCategory.setBrand(brand);

        return parentCategoryRepository.save(parentCategory);
    }

    @Override
    @Transactional
    public ParentCategory updateParentCategory(Long id, ParentCategoryRequest parentCategoryRequest) throws ResponseError {
        ParentCategory oldParentCategory = parentCategoryRepository.findById(id)
                .orElseThrow(() -> new ResponseError(
                        "Parent category with id: " + id + " not found !!!",
                        HttpStatus.NOT_FOUND.value()
                ));

        parentCategoryRequest.setName(parentCategoryRequest.getName().toUpperCase());

        Optional<ParentCategory> parentCategoryExist = parentCategoryRepository.findByNameAndBrandId(parentCategoryRequest.getName(), oldParentCategory.getBrand().getId());

        if (!parentCategoryExist.isPresent() || parentCategoryExist.get().getName().equals(oldParentCategory.getName())) {
            String emailAdmin = methodUtils.getEmailFromTokenOfAdmin();

            oldParentCategory.setUpdateBy(emailAdmin);
            oldParentCategory.setName(parentCategoryRequest.getName());

            return parentCategoryRepository.save(oldParentCategory);
        } else {
            throw new ResponseError("Parent category with name " + parentCategoryRequest.getName() + " of brand " + oldParentCategory.getBrand().getName() + " already exist !!!",
                    HttpStatus.CONFLICT.value());
        }
    }

    @Override
    @Transactional
    public Response deleteParentCategory(Long id) throws ResponseError {
        ParentCategory parentCategory = parentCategoryRepository.findById(id)
                .orElseThrow(() -> new ResponseError(
                        "Parent category not found with id: " + id,
                        HttpStatus.NOT_FOUND.value()
                ));
        parentCategoryRepository.delete(parentCategory);

        Response response = new Response();
        response.setMessage("Delete parent category success !!!");
        response.setStatus(HttpStatus.OK.value());

        return response;
    }

    @Override
    public Set<ParentCategory> getAllParentCategoriesByBrandId(Long brandId) throws ResponseError {
        Brand brand = brandRepository.findById(brandId)
                .orElseThrow(() -> new ResponseError(
                        "Brand not found with id: " + brandId,
                        HttpStatus.NOT_FOUND.value()
                ));
        return parentCategoryRepository.getAllParentCategoryByBrandId(brand.getId());
    }
}
