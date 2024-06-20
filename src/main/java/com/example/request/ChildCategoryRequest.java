package com.example.request;

public class ChildCategoryRequest {
    private String name;
    private Long parentCategoryId;

    public ChildCategoryRequest() {
    }

    public ChildCategoryRequest(String name, Long parentCategoryId) {
        this.name = name;
        this.parentCategoryId = parentCategoryId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getParentCategoryId() {
        return parentCategoryId;
    }

    public void setParentCategoryId(Long parentCategoryId) {
        this.parentCategoryId = parentCategoryId;
    }
}
