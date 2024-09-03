package com.example.api;

import com.example.Entity.Product;
import com.example.Entity.Size;
import com.example.exception.CustomException;
import com.example.response.ListProductsResponse;
import com.example.response.ResponseData;
import com.example.service.implement.ProductServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RestController("products")
@RequestMapping("/api")
public class ApiProduct {

    @Autowired
    private ProductServiceImpl productService;

    // CALL SUCCESS
    @GetMapping("/products")
    public ResponseEntity<?> filterProductsUser(@RequestParam(value = "name", required = false) String name,
                                                @RequestParam(value = "brandId", required = false) Long brandId,
                                                @RequestParam(value = "parentCategoryId", required = false) Long parentCategoryId,
                                                @RequestParam(value = "childCategoryId", required = false) Long childCategoryId,
                                                @RequestParam(value = "color", required = false) String color,
                                                @RequestParam(value = "minPrice", required = false) Double minPrice,
                                                @RequestParam(value = "maxPrice", required = false) Double maxPrice,
                                                @RequestParam(value = "sort", required = false) String sort,
                                                @RequestParam(value = "sale", required = false) Boolean sale,
                                                @RequestParam("pageIndex") int pageIndex,
                                                @RequestParam("pageSize") int pageSize) {
        ListProductsResponse listProductsResponse = productService.filterProducts(name, brandId, parentCategoryId, childCategoryId,
                color, minPrice, maxPrice, sort, sale, pageIndex, pageSize);

        ResponseData<ListProductsResponse> response = new ResponseData<>();
        response.setMessage("Filter products success !!!");
        response.setStatus(HttpStatus.OK.value());
        response.setResults(listProductsResponse);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // CALL SUCCESS
    @GetMapping("/all/products")
    public ResponseEntity<?> getAllProducts() {
        return new ResponseEntity<>(productService.getAllProduct(), HttpStatus.OK);
    }

    // CALL SUCCESS
    @GetMapping("/products/featured")
    public ResponseEntity<?> getTwelveNewestProducts() throws CustomException {
        ListProductsResponse listProductsResponse = productService.getTwelveNewestProducts();

        ResponseData<ListProductsResponse> responseData = new ResponseData<>();
        responseData.setStatus(HttpStatus.OK.value());
        responseData.setMessage("Get products featured success !!!");
        responseData.setResults(listProductsResponse);

        return new ResponseEntity<>(responseData, HttpStatus.OK);
    }

    // CALL SUCCESS
    @GetMapping("/products/bestseller")
    public ResponseEntity<?> getTwelveProductsLeastQuantity() throws CustomException {
        ListProductsResponse listProductsResponse = productService.getTwelveProductsLeastQuantity();

        ResponseData<ListProductsResponse> responseData = new ResponseData<>();
        responseData.setStatus(HttpStatus.OK.value());
        responseData.setMessage("Get products featured success !!!");
        responseData.setResults(listProductsResponse);

        return new ResponseEntity<>(responseData, HttpStatus.OK);
    }

    // CALL SUCCESS
    @GetMapping("/product/highest/price")
    public ResponseEntity<?> getHighestPriceOfProduct() {
        ResponseData<Long> responseData = new ResponseData<>();
        responseData.setResults(productService.getTheHighestPriceOfProduct());
        responseData.setMessage("Get the price highest success !!!");
        responseData.setStatus(HttpStatus.OK.value());

        return new ResponseEntity<>(responseData, HttpStatus.OK);
    }

    // CALL SUCCESS
    @GetMapping("/product/detail")
    public ResponseEntity<?> getDetailProduct(@RequestParam("id") Long id) throws CustomException {
        Product product = productService.getById(id);

        ResponseData<Product> responseData = new ResponseData<>();
        responseData.setResults(product);
        responseData.setMessage("Get the product detail success !!!");
        responseData.setStatus(HttpStatus.OK.value());

        return new ResponseEntity<>(responseData, HttpStatus.OK);
    }

    // CALL SUCCESS
    @GetMapping("/products/similar")
    public ResponseEntity<?> getSimilarProducts(@RequestParam("brandId") Long brandId,
                                                @RequestParam("productId") Long productId) {
        ListProductsResponse listProductsResponse = productService.getSimilarProductsByBrandId(brandId, productId);

        ResponseData<ListProductsResponse> responseData = new ResponseData<>();
        responseData.setStatus(HttpStatus.OK.value());
        responseData.setMessage("Get similar products success !!!");
        responseData.setResults(listProductsResponse);

        return new ResponseEntity<>(responseData, HttpStatus.OK);
    }

    // CALL SUCCESS
    @GetMapping("/products/also/like")
    public ResponseEntity<?> getProductsAlsoLike() {
        ListProductsResponse listProductsResponse = productService.getTwelveProductsMostQuantity();

        ResponseData<ListProductsResponse> responseData = new ResponseData<>();
        responseData.setStatus(HttpStatus.OK.value());
        responseData.setMessage("Get products also like success !!!");
        responseData.setResults(listProductsResponse);

        return new ResponseEntity<>(responseData, HttpStatus.OK);
    }

    @GetMapping("/product/sizes")
    public ResponseEntity<?> getSizesOfProduct(@RequestParam("id") Long id) throws CustomException {
        Set<Size> sizes = productService.getSizesOfProduct(id);

        ResponseData<Set<Size>> responseData = new ResponseData<>();
        responseData.setStatus(HttpStatus.OK.value());
        responseData.setMessage("Get sizes of products success !!!");
        responseData.setResults(sizes);

        return new ResponseEntity<>(responseData, HttpStatus.OK);
    }
}
