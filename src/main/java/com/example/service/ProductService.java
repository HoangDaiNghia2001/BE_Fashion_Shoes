package com.example.service;

import com.example.Entity.Product;
import com.example.exception.CustomException;
import com.example.request.ProductRequest;
import com.example.response.*;

import java.io.IOException;
import java.util.List;

public interface ProductService {
    Product createProduct(ProductRequest productRequest) throws ResponseError;

    Product updateProduct(Long id, ProductRequest productRequest) throws ResponseError;

    Response deleteProduct(Long id) throws ResponseError;

    Response deleteSomeProducts (List<Long> listIdProducts) throws ResponseError;

    ListProductsResponse filterProductsByAdmin(String name, Long brandId, Long parentCategoryId, Long childCategoryId, String color,
                                               Integer discountedPercent, String createBy, String updateBy, String code, Double price, int pageIndex, int pageSize) throws ResponseError;

    List<Product> getAllProduct();

    ListProductsResponse getTwelveNewestProducts() throws ResponseError;

    ListProductsResponse getTwelveProductsLeastQuantity();

    ListProductsResponse getTwelveProductsMostQuantity();

    ListProductsResponse filterProducts(String name, Long brandId, Long parentCategoryId, Long childCategoryId, String color,
                                       Double minPrice, Double maxPrice, String sort, Boolean sale, int pageIndex, int pageSize);

    Product getDetailProduct(Long id) throws ResponseError;

    Long getTheHighestPriceOfProduct();

    ListProductsResponse getSimilarProductsByBrandId(Long brandId, Long productId);

    List<QuantityByBrandResponse> countQuantityByBrand();

    List<TopBestSellerResponse> topTenBestSeller();

    long stock();
}
