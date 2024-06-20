package com.example.response;

import java.util.List;

public class ParentCategoryResponse{
    private Long id;
    private Long brandId;
    private String name;
    List<ChildCategoryResponse> childCategoryResponseList;

    public ParentCategoryResponse() {
    }

    public ParentCategoryResponse(Long id, Long brandId, String name, List<ChildCategoryResponse> childCategoryResponseList) {
        this.id = id;
        this.brandId = brandId;
        this.name = name;
        this.childCategoryResponseList = childCategoryResponseList;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getBrandId() {
        return brandId;
    }

    public void setBrandId(Long brandId) {
        this.brandId = brandId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<ChildCategoryResponse> getChildCategoryResponseList() {
        return childCategoryResponseList;
    }

    public void setChildCategoryResponseList(List<ChildCategoryResponse> childCategoryResponseList) {
        this.childCategoryResponseList = childCategoryResponseList;
    }
}
