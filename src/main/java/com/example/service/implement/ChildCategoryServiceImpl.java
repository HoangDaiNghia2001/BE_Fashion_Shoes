package com.example.service.implement;

import com.example.Entity.ChildCategory;
import com.example.Entity.ParentCategory;
import com.example.repository.ChildCategoryRepository;
import com.example.repository.ParentCategoryRepository;
import com.example.request.ChildCategoryRequest;
import com.example.response.Response;
import com.example.response.ResponseError;
import com.example.service.ChildCategoryService;
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
    private ParentCategoryRepository parentCategoryRepository;
    @Autowired
    private ChildCategoryRepository childCategoryRepository;
    @Autowired
    private MethodUtils methodUtils;

    @Override
    @Transactional
    public ChildCategory createChildCategory(ChildCategoryRequest childCategoryRequest) throws ResponseError {
        ParentCategory parentCategory = parentCategoryRepository.findById(childCategoryRequest.getParentCategoryId())
                .orElseThrow(() -> new ResponseError(
                        "Parent category not found !!!",
                        HttpStatus.NOT_FOUND.value()
                ));

        childCategoryRequest.setName(childCategoryRequest.getName().toUpperCase());

        Optional<ChildCategory> childCategoryExist = childCategoryRepository.findByNameAndParentCategoryId(childCategoryRequest.getName(), parentCategory.getId());

        if (childCategoryExist.isPresent()) {
            throw new ResponseError(
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
    public ChildCategory updateChildCategory(Long id, ChildCategoryRequest childCategoryRequest) throws ResponseError {
        ChildCategory oldChildCategory = childCategoryRepository.findById(id)
                .orElseThrow(() -> new ResponseError(
                        "Child category not found !!!",
                        HttpStatus.NOT_FOUND.value()
                ));

        childCategoryRequest.setName(childCategoryRequest.getName().toUpperCase());

        Optional<ChildCategory> childCategoryExist = childCategoryRepository.findByNameAndParentCategoryId(childCategoryRequest.getName(), oldChildCategory.getParentCategory().getId());

        if (!childCategoryExist.isPresent() || childCategoryExist.get().getName().equals(oldChildCategory.getName())) {
            String emailAdmin = methodUtils.getEmailFromTokenOfAdmin();

            oldChildCategory.setName(childCategoryRequest.getName());
            oldChildCategory.setUpdateBy(emailAdmin);

            return childCategoryRepository.save(oldChildCategory);
        } else {
            throw new ResponseError(
                    "Child category with name: " + childCategoryRequest.getName() + " already exist !!!",
                    HttpStatus.CONFLICT.value());
        }
    }

    @Override
    @Transactional
    public Response deleteChildCategory(Long id) throws ResponseError {
        ChildCategory childCategory = childCategoryRepository.findById(id)
                .orElseThrow(() -> new ResponseError(
                        "Child category not found with id: " + id,
                        HttpStatus.NOT_FOUND.value()
                ));
        childCategoryRepository.delete(childCategory);
        Response response = new Response();
        response.setMessage("Delete child category success !!!");
        response.setStatus(HttpStatus.OK.value());

        return response;
    }

    @Override
    public List<ChildCategory> getAllChildCategoriesByParentCategoryId(Long parentCategoryId) throws ResponseError {
        ParentCategory parentCategory = parentCategoryRepository.findById(parentCategoryId)
                .orElseThrow(() -> new ResponseError(
                        "Parent category not found with id: " + parentCategoryId,
                        HttpStatus.NOT_FOUND.value()
                ));

        return childCategoryRepository.getAllChildCategoriesByParentCategoryId(parentCategory.getId());
    }
}
