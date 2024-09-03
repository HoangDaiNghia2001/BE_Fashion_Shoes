package com.example.service.implement;

import com.example.Entity.ChildCategory;
import com.example.Entity.ParentCategory;
import com.example.repository.ChildCategoryRepository;
import com.example.request.ChildCategoryRequest;
import com.example.response.Response;
import com.example.exception.CustomException;
import com.example.service.ChildCategoryService;
import com.example.service.ParentCategoryService;
import com.example.util.MethodUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ChildCategoryServiceImpl implements ChildCategoryService {
    @Autowired
    private ParentCategoryService parentCategoryService;
    @Autowired
    private ChildCategoryRepository childCategoryRepository;
    @Autowired
    private MethodUtils methodUtils;

    @Override
    public ChildCategory getById(Long id) throws CustomException {
        ChildCategory childCategory = childCategoryRepository.findById(id)
                .orElseThrow(() -> new CustomException(
                        "Child category not found !!!",
                        HttpStatus.NOT_FOUND.value()
                ));
        return childCategory;
    }

    @Override
    public ChildCategory getByIdAndParentCategoryId(Long id, Long parentId) throws CustomException {
        ChildCategory childCategory = childCategoryRepository.findByIdAndParentCategoryId(id, parentId)
                .orElseThrow(() -> new CustomException(
                        new StringBuilder("Child category with id ")
                                .append(id)
                                .append(" and have parent category id ")
                                .append(parentId)
                                .append(" not exist !!!")
                                .toString(),
                        HttpStatus.NOT_FOUND.value()
                ));
        return childCategory;
    }

    @Override
    @Transactional
    public ChildCategory createChildCategory(ChildCategoryRequest childCategoryRequest) throws CustomException {
        ParentCategory parentCategory = parentCategoryService.getById(childCategoryRequest.getParentCategoryId());

        childCategoryRequest.setName(childCategoryRequest.getName().toUpperCase());

        Optional<ChildCategory> childCategoryExist = childCategoryRepository.findByNameAndParentCategoryId(childCategoryRequest.getName(), parentCategory.getId());

        if (childCategoryExist.isPresent()) {
            throw new CustomException(
                    "Child category with name: " + childCategoryRequest.getName() + " already exist !!!",
                    HttpStatus.CONFLICT.value()
            );
        }
        String emailAdmin = methodUtils.getEmailFromTokenOfAdmin();

        ChildCategory childCategory = new ChildCategory();
        childCategory.setName(childCategoryRequest.getName());
        childCategory.setParentCategory(parentCategory);
        childCategory.setCreatedBy(emailAdmin);

        return childCategoryRepository.save(childCategory);
    }

    @Override
    @Transactional
    public ChildCategory updateChildCategory(Long id, ChildCategoryRequest childCategoryRequest) throws CustomException {
        ChildCategory oldChildCategory = this.getById(id);

        if(!oldChildCategory.getParentCategory().getId().equals(childCategoryRequest.getParentCategoryId())){
            throw new CustomException(
                    "Parent category id request does not match with parent category id of this child category",
                    HttpStatus.BAD_REQUEST.value()
            );
        }

        childCategoryRequest.setName(childCategoryRequest.getName().toUpperCase());

        // check name unique
        Optional<ChildCategory> childCategoryExist = childCategoryRepository.findByNameAndParentCategoryId(childCategoryRequest.getName(), oldChildCategory.getParentCategory().getId());

        if (!childCategoryExist.isPresent() || childCategoryExist.get().getName().equals(oldChildCategory.getName())) {
            String emailAdmin = methodUtils.getEmailFromTokenOfAdmin();

            oldChildCategory.setName(childCategoryRequest.getName());
            oldChildCategory.setUpdateBy(emailAdmin);

            return childCategoryRepository.save(oldChildCategory);
        } else {
            throw new CustomException(
                    "Child category with name: " + childCategoryRequest.getName() + " already exist !!!",
                    HttpStatus.CONFLICT.value());
        }
    }

    @Override
    @Transactional
    public Response deleteChildCategory(Long id) throws CustomException {
        ChildCategory childCategory = this.getById(id);
        childCategoryRepository.delete(childCategory);
        Response response = new Response();
        response.setMessage("Delete child category success !!!");
        response.setStatus(HttpStatus.OK.value());

        return response;
    }

    @Override
    public List<ChildCategory> getAllChildCategoriesByParentCategoryId(Long parentCategoryId) throws CustomException {
        ParentCategory parentCategory = parentCategoryService.getById(parentCategoryId);

        return childCategoryRepository.getAllChildCategoriesByParentCategoryId(parentCategory.getId());
    }
}
