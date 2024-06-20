package com.example.api;

import com.example.exception.CustomException;
import com.example.response.ProductResponse;
import com.example.response.ResponseData;
import com.example.service.implement.ProductServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("products")
@RequestMapping("/api")
//@CrossOrigin(origins = {"http://localhost:3000/","http://localhost:3001/","https://fashion-shoes.vercel.app/"}, allowCredentials = "true")
public class ApiProduct {

    @Autowired
    private ProductServiceImpl productService;

    //     CALL SUCCESS
    @GetMapping("/products")
    public ResponseEntity<?> filterProductsUser(@RequestParam(value = "name",required = false) String name,
                                                @RequestParam(value = "brandId",required = false) Long brandId,
                                                @RequestParam(value = "parentCategoryId",required = false) Long parentCategoryId,
                                                @RequestParam(value = "childCategoryId",required = false) Long childCategoryId,
                                                @RequestParam(value = "color",required = false) String color,
                                                @RequestParam(value = "minPrice",required = false) Double minPrice,
                                                @RequestParam(value = "maxPrice",required = false) Double maxPrice,
                                                @RequestParam(value = "sort",required = false) String sort,
                                                @RequestParam(value = "sale",required = false) Boolean sale,
                                                @RequestParam("pageIndex") int pageIndex,
                                                @RequestParam("pageSize") int pageSize) {
        ProductResponse productResponse = productService.filterProduct(name, brandId, parentCategoryId, childCategoryId,
                color, minPrice, maxPrice, sort, sale, pageIndex, pageSize);

        ResponseData<ProductResponse> response = new ResponseData<>();
        response.setMessage("Filter products success !!!");
        response.setSuccess(true);
        response.setResults(productResponse);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // CALL SUCCESS
    @GetMapping("/all/products")
    public ResponseEntity<?> getAllProducts(){
        return new ResponseEntity<>(productService.getAllProduct(),HttpStatus.OK);
    }

    // CALL SUCCESS
    @GetMapping("/product/images")
    public ResponseEntity<?> getImageBase64OfProduct(@RequestParam("id") Long id) throws CustomException {
        List<String> secondaryImages = productService.getSecondaryImagesBase64(id);

        String mainImage = productService.getMainImageBas64(id);

        secondaryImages.add(0,mainImage);

        ResponseData<List<String>> responseData = new ResponseData<>();
        responseData.setResults(secondaryImages);
        responseData.setMessage("Get main image success !!!");
        responseData.setSuccess(true);

        return new ResponseEntity<>(responseData, HttpStatus.OK);
    }

    // CALL SUCCESS
    @GetMapping("/products/featured")
    public ResponseEntity<?> getTwelveNewestProducts() throws CustomException {
        ProductResponse productResponse = productService.getTwelveNewestProducts();

        ResponseData<ProductResponse> responseData = new ResponseData<>();
        responseData.setSuccess(true);
        responseData.setMessage("Get products featured success !!!");
        responseData.setResults(productResponse);

        return new ResponseEntity<>(responseData, HttpStatus.OK);
    }

    // CALL SUCCESS
    @GetMapping("/products/bestseller")
    public ResponseEntity<?> getTwelveProductsLeastQuantity() throws CustomException {
        ProductResponse productResponse = productService.getTwelveProductsLeastQuantity();

        ResponseData<ProductResponse> responseData = new ResponseData<>();
        responseData.setSuccess(true);
        responseData.setMessage("Get products featured success !!!");
        responseData.setResults(productResponse);

        return new ResponseEntity<>(responseData, HttpStatus.OK);
    }

    // CALL SUCCESS
    @GetMapping("/product/highest/price")
    public ResponseEntity<?> getHighestPriceOfProduct(){
        ResponseData<Long> responseData = new ResponseData<>();
        responseData.setResults(productService.getTheHighestPriceOfProduct());
        responseData.setMessage("Get the price highest success !!!");
        responseData.setSuccess(true);

        return new ResponseEntity<>(responseData, HttpStatus.OK);
    }

    // CALL SUCCESS
    @GetMapping("/product/detail")
    public ResponseEntity<?> getDetailProduct(@RequestParam("id") Long id) throws CustomException {
        return new ResponseEntity<>(productService.getDetailProduct(id), HttpStatus.OK);
    }

    // CALL SUCCESS
    @GetMapping("/products/similar")
    public ResponseEntity<?> getSimilarProducts(@RequestParam("brandId") Long brandId,
                                                @RequestParam("productId")Long productId){
        ProductResponse productResponse = productService.getSimilarProductsByBrandId(brandId, productId);

        ResponseData<ProductResponse> responseData = new ResponseData<>();
        responseData.setSuccess(true);
        responseData.setMessage("Get similar products success !!!");
        responseData.setResults(productResponse);

        return new ResponseEntity<>(responseData, HttpStatus.OK);
    }

    // CALL SUCCESS
    @GetMapping("/products/also/like")
    public ResponseEntity<?> getProductsAlsoLike(){
        ProductResponse productResponse = productService.getTwelveProductsMostQuantity();

        ResponseData<ProductResponse> responseData = new ResponseData<>();
        responseData.setSuccess(true);
        responseData.setMessage("Get products also like success !!!");
        responseData.setResults(productResponse);

        return new ResponseEntity<>(responseData, HttpStatus.OK);
    }
}
