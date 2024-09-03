package com.example.service.implement;

import com.example.Entity.Brand;
import com.example.Entity.ParentCategory;
import com.example.repository.ParentCategoryRepository;
import com.example.request.ParentCategoryRequest;
import com.example.response.Response;
import com.example.exception.CustomException;
import com.example.service.BrandService;
import com.example.service.ParentCategoryService;
import com.example.util.MethodUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Set;

@Service
public class ParentCategoryServiceImpl implements ParentCategoryService {
    @Autowired
    private BrandService brandService;
    @Autowired
    private ParentCategoryRepository parentCategoryRepository;
    @Autowired
    private MethodUtils methodUtils;

    @Override
    public ParentCategory getById(Long id) throws CustomException {
        ParentCategory parentCategory = parentCategoryRepository.findById(id)
                .orElseThrow(() -> new CustomException(
                        "Parent category with id: " + id + " not found !!!",
                        HttpStatus.NOT_FOUND.value()
                ));
        return parentCategory;
    }

    @Override
    public ParentCategory getByIdAndBrandId(Long id, Long brandId) throws CustomException {
        ParentCategory parentCategory = parentCategoryRepository.findByIdAndAndBrandId(id, brandId)
                .orElseThrow(() -> new CustomException(
                        new StringBuilder("Parent category with id ")
                                .append(id)
                                .append(" and have brand id ")
                                .append(brandId)
                                .append(" not exist !!!")
                                .toString(),
                        HttpStatus.NOT_FOUND.value()
                ));
        return parentCategory;
    }

    @Override
    @Transactional
    public ParentCategory createdParentCategory(ParentCategoryRequest parentCategoryRequest) throws CustomException {
        Brand brand = brandService.getById(parentCategoryRequest.getBrandId());

        parentCategoryRequest.setName(parentCategoryRequest.getName().toUpperCase());

        Optional<ParentCategory> parentCategoryExist = parentCategoryRepository.findByNameAndBrandId(parentCategoryRequest.getName(), brand.getId());

        if (parentCategoryExist.isPresent()) {
            throw new CustomException("Parent category with name " + parentCategoryRequest.getName() + " of brand " + brand.getName() + " already exist !!!",
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
    public ParentCategory updateParentCategory(Long id, ParentCategoryRequest parentCategoryRequest) throws CustomException {
        ParentCategory oldParentCategory = this.getById(id);

        if(!oldParentCategory.getBrand().getId().equals(parentCategoryRequest.getBrandId())){
            throw new CustomException(
                    "Brand id request does not match with brand id of this parent category",
                    HttpStatus.BAD_REQUEST.value()
            );
        }

        parentCategoryRequest.setName(parentCategoryRequest.getName().toUpperCase());

        Optional<ParentCategory> parentCategoryExist = parentCategoryRepository.findByNameAndBrandId(parentCategoryRequest.getName(), oldParentCategory.getBrand().getId());

        if (!parentCategoryExist.isPresent() || parentCategoryExist.get().getName().equals(oldParentCategory.getName())) {
            String emailAdmin = methodUtils.getEmailFromTokenOfAdmin();

            oldParentCategory.setUpdateBy(emailAdmin);
            oldParentCategory.setName(parentCategoryRequest.getName());

            return parentCategoryRepository.save(oldParentCategory);
        } else {
            throw new CustomException("Parent category with name " + parentCategoryRequest.getName() + " of brand " + oldParentCategory.getBrand().getName() + " already exist !!!",
                    HttpStatus.CONFLICT.value());
        }
    }

    @Override
    @Transactional
    public Response deleteParentCategory(Long id) throws CustomException {
        ParentCategory parentCategory = this.getById(id);
        parentCategoryRepository.delete(parentCategory);

        Response response = new Response();
        response.setMessage("Delete parent category success !!!");
        response.setStatus(HttpStatus.OK.value());

        return response;
    }

    @Override
    public Set<ParentCategory> getAllParentCategoriesByBrandId(Long brandId) throws CustomException {
        Brand brand = brandService.getById(brandId);
        return parentCategoryRepository.getAllParentCategoryByBrandId(brand.getId());
    }
}
