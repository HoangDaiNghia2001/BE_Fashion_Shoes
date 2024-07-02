package com.example.api.admin;

import com.example.Entity.Product;
import com.example.exception.CustomException;
import com.example.request.ProductRequest;
import com.example.response.*;
import com.example.service.implement.ProductServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController("productOfRoleAdmin")
@RequestMapping("/api/admin")
public class ApiProduct {

    @Autowired
    private ProductServiceImpl productService;

    // CALL SUCCESS
    @PostMapping("/product")
    public ResponseEntity<?> createProduct(@RequestBody ProductRequest productRequest) throws ResponseError {
        Product product = productService.createProduct(productRequest);

        ResponseData<Product> productResponse = new ResponseData<>();
        productResponse.setResults(product);
        productResponse.setMessage("Product created success !!!");
        productResponse.setStatus(HttpStatus.OK.value());

        return new ResponseEntity<>(productResponse, HttpStatus.OK);
    }

    // CALL SUCCESS
    @PutMapping("/product")
    public ResponseEntity<?> updateProduct(@RequestParam("id") Long id,
                                           @RequestBody ProductRequest productRequest) throws ResponseError {
        Product product = productService.updateProduct(id, productRequest);

        ResponseData<Product> productResponse = new ResponseData<>();
        productResponse.setResults(product);
        productResponse.setMessage("Product updated success !!!");
        productResponse.setStatus(HttpStatus.OK.value());

        return new ResponseEntity<>(productResponse, HttpStatus.OK);
    }

    // CALL SUCCESS
    @DeleteMapping("/product")
    public ResponseEntity<?> deleteProduct(@RequestParam("id") Long id) throws ResponseError {
        Response response = productService.deleteProduct(id);

        response.setMessage("Delete product with id " + id + " success !!!");
        response.setStatus(HttpStatus.OK.value());

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // CALL SUCCESS
    @DeleteMapping("/products/{listIdProducts}")
    public ResponseEntity<?> deleteSomeProducts(@PathVariable List<Long> listIdProducts) throws ResponseError {
        Response response =productService.deleteSomeProducts(listIdProducts);

        response.setMessage("Delete list product have id " + listIdProducts + " success !!!");
        response.setStatus(HttpStatus.OK.value());

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // CALL SUCCESS
    @GetMapping("/products")
    public ResponseEntity<?> filterProducts(@RequestParam(value = "name", required = false) String name,
                                            @RequestParam(value = "brandId", required = false) Long brandId,
                                            @RequestParam(value = "parentCategoryId", required = false) Long parentCategoryId,
                                            @RequestParam(value = "childCategoryId", required = false) Long childCategoryId,
                                            @RequestParam(value = "color", required = false) String color,
                                            @RequestParam(value = "discountedPercent", required = false) Integer discountedPercent,
                                            @RequestParam(value = "createBy", required = false) String createBy,
                                            @RequestParam(value = "updateBy", required = false) String updateBy,
                                            @RequestParam(value = "code", required = false) String code,
                                            @RequestParam(value = "price", required = false) Double price,
                                            @RequestParam("pageIndex") int pageIndex,
                                            @RequestParam("pageSize") int pageSize) throws ResponseError {
        ListProductsResponse listProductsResponse = productService.filterProductsByAdmin(name, brandId, parentCategoryId, childCategoryId,
                color, discountedPercent, createBy, updateBy, code, price, pageIndex, pageSize);

        ResponseData<ListProductsResponse> responseData = new ResponseData<>();
        responseData.setResults(listProductsResponse);
        responseData.setMessage("Filter products success !!!");
        responseData.setStatus(HttpStatus.OK.value());

        return new ResponseEntity<>(responseData, HttpStatus.OK);
    }

    @GetMapping("/products/quantity")
    public ResponseEntity<?> quantityByBrand() {
        List<QuantityByBrandResponse> quantity = productService.countQuantityByBrand();

        ResponseData<List<QuantityByBrandResponse>> responseData = new ResponseData<>();
        responseData.setStatus(HttpStatus.OK.value());
        responseData.setMessage("Count quantity by Brand success !!!");
        responseData.setResults(quantity);

        return new ResponseEntity<>(responseData, HttpStatus.OK);
    }

    @GetMapping("/products/top-ten/best-seller")
    public ResponseEntity<?> getTopTenBestSeller(){
        List<TopBestSellerResponse> topTen = productService.topTenBestSeller();

        ResponseData<List<TopBestSellerResponse>> responseData = new ResponseData<>();
        responseData.setStatus(HttpStatus.OK.value());
        responseData.setMessage("Get top 10 products best seller success !!!");
        responseData.setResults(topTen);

        return new ResponseEntity<>(responseData, HttpStatus.OK);
    }

    @GetMapping("/products/stock")
    public ResponseEntity<?> stock(){
        long total = productService.stock();

        ResponseData<Long> responseData = new ResponseData<>();
        responseData.setStatus(HttpStatus.OK.value());
        responseData.setMessage("Get total products in stock success !!!");
        responseData.setResults(total);

        return new ResponseEntity<>(responseData, HttpStatus.OK);
    }


}
