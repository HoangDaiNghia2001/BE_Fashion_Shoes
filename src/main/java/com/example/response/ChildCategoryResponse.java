package com.example.response;

public class ChildCategoryResponse {
    private Long id;
    private Long parentCategoryId;
    private String name;

    public ChildCategoryResponse() {
    }

    public ChildCategoryResponse(Long id, Long parentCategoryId, String name) {
        this.id = id;
        this.parentCategoryId = parentCategoryId;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getParentCategoryId() {
        return parentCategoryId;
    }

    public void setParentCategoryId(Long parentCategoryId) {
        this.parentCategoryId = parentCategoryId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
