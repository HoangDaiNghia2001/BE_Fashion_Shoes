package com.example.response;

import com.example.Entity.Product;

import java.util.List;

public class ProductResponse {
    private List<Product> listProducts;
    private Long totalProduct;

    public ProductResponse(List<Product> listProducts, Long totalProduct) {
        this.listProducts = listProducts;
        this.totalProduct = totalProduct;
    }

    public ProductResponse() {
    }

    public List<Product> getListProducts() {
        return listProducts;
    }

    public void setListProducts(List<Product> listProducts) {
        this.listProducts = listProducts;
    }

    public Long getTotalProduct() {
        return totalProduct;
    }

    public void setTotalProduct(Long totalProduct) {
        this.totalProduct = totalProduct;
    }
}
