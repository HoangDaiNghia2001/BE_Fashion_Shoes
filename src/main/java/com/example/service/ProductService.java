package com.example.service;

import com.example.Entity.Product;
import com.example.Entity.Size;
import com.example.exception.CustomException;
import com.example.request.ProductRequest;
import com.example.response.*;

import java.util.List;
import java.util.Set;

public interface ProductService {
    Product getById(Long id) throws CustomException;
    Product createProduct(ProductRequest productRequest) throws CustomException;

    Product updateProduct(Long id, ProductRequest productRequest) throws CustomException;

    Response deleteProduct(Long id) throws CustomException;

    Response deleteSomeProducts (List<Long> listIdProducts) throws CustomException;

    ListProductsResponse filterProductsByAdmin(String name, Long brandId, Long parentCategoryId, Long childCategoryId, String color,
                                               Integer discountedPercent, String createBy, String updateBy, String code, Double price, int pageIndex, int pageSize) throws CustomException;

    List<Product> getAllProduct();

    ListProductsResponse getTwelveNewestProducts() throws CustomException;

    ListProductsResponse getTwelveProductsLeastQuantity();

    ListProductsResponse getTwelveProductsMostQuantity();

    ListProductsResponse filterProducts(String name, Long brandId, Long parentCategoryId, Long childCategoryId, String color,
                                       Double minPrice, Double maxPrice, String sort, Boolean sale, int pageIndex, int pageSize);

    Long getTheHighestPriceOfProduct();

    ListProductsResponse getSimilarProductsByBrandId(Long brandId, Long productId);

    List<QuantityByBrandResponse> countQuantityByBrand();

    List<TopBestSellerResponse> topTenBestSeller();

    long stock();

    Set<Size> getSizesOfProduct(Long id) throws CustomException;
}
