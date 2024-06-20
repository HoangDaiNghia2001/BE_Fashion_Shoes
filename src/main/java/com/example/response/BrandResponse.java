package com.example.response;

import java.util.List;

public class BrandResponse{
    private Long id;
    private String name;
    private List<ParentCategoryResponse> parentCategoryResponseList;

    public BrandResponse() {
    }

    public BrandResponse(Long id, String name, List<ParentCategoryResponse> parentCategoryResponseList) {
        this.id = id;
        this.name = name;
        this.parentCategoryResponseList = parentCategoryResponseList;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<ParentCategoryResponse> getParentCategoryResponseList() {
        return parentCategoryResponseList;
    }

    public void setParentCategoryResponseList(List<ParentCategoryResponse> parentCategoryResponseList) {
        this.parentCategoryResponseList = parentCategoryResponseList;
    }
}
