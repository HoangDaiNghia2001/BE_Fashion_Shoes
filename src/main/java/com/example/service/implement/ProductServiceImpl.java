package com.example.service.implement;

import com.example.Entity.*;
import com.example.exception.CustomException;
import com.example.mapper.ProductMapper;
import com.example.repository.ProductRepository;
import com.example.request.ProductRequest;
import com.example.response.*;
import com.example.service.BrandService;
import com.example.service.ChildCategoryService;
import com.example.service.ParentCategoryService;
import com.example.service.ProductService;
import com.example.util.MethodUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService {
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private BrandService brandService;
    @Autowired
    private ParentCategoryService parentCategoryService;
    @Autowired
    private ChildCategoryService childCategoryService;
    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private MethodUtils methodUtils;

    private String generateUniqueCode(String brandName) {
        String code;
        Product existingProduct;
        do {
            String uuid = UUID.randomUUID().toString().replaceAll("-", "").substring(0, 6);
            code = brandName + "_" + uuid;
            existingProduct = productRepository.findByCode(code);
        } while (existingProduct != null);

        return code.toUpperCase();
    }

    private long calculatePrice(double price, int discountedPercent){
        return Math.round(price - ((double) discountedPercent / 100) * price);
    }

    private int calculateQuantity(ProductRequest productRequest){
        return productRequest.getSizes().stream()
                .mapToInt(Size::getQuantity)
                .sum();
    }
    @Override
    public Product getById(Long id) throws CustomException {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new CustomException(
                        "Product not found with id: " + id,
                        HttpStatus.NOT_FOUND.value()
                ));
        return product;
    }

    @Override
    @Transactional
    public Product createProduct(ProductRequest productRequest) throws CustomException {
        Brand brand = brandService.getById(productRequest.getBrandId());

        ParentCategory parentCategory = parentCategoryService.getByIdAndBrandId(productRequest.getParentCategoryId(), brand.getId());

        ChildCategory childCategory = childCategoryService.getByIdAndParentCategoryId(productRequest.getChildCategoryId(), parentCategory.getId());

        String emailAdmin = methodUtils.getEmailFromTokenOfAdmin();

        Product product = new Product();
        productMapper.productRequestToProduct(productRequest, product);

        product.setSizes(productRequest.getSizes());
        product.setCode(generateUniqueCode(brand.getName()));
        product.setCreatedBy(emailAdmin);
        product.setDiscountedPrice(this.calculatePrice(productRequest.getPrice(), productRequest.getDiscountedPercent()));
        product.setQuantity(this.calculateQuantity(productRequest));
        product.setBrandProduct(brand);
        product.setParentCategoryOfProduct(parentCategory);
        product.setChildCategoryOfProduct(childCategory);
        product.setColor(productRequest.getColor().toUpperCase());

        return productRepository.save(product);
    }

    @Override
    @Transactional
    public Product updateProduct(Long id, ProductRequest productRequest) throws CustomException {
        System.out.println(productRequest);
        Product oldProduct = this.getById(id);

        Brand brand = brandService.getById(productRequest.getBrandId());

        ParentCategory parentCategory = parentCategoryService.getByIdAndBrandId(productRequest.getParentCategoryId(), brand.getId());

        ChildCategory childCategory = childCategoryService.getByIdAndParentCategoryId(productRequest.getChildCategoryId(), parentCategory.getId());

        String emailAdmin = methodUtils.getEmailFromTokenOfAdmin();

        productRequest.setColor(productRequest.getColor().toUpperCase());
        productMapper.productRequestToProduct(productRequest, oldProduct);
        oldProduct.setSizes(productRequest.getSizes());
        oldProduct.setUpdateBy(emailAdmin);
        oldProduct.setDiscountedPrice(this.calculatePrice(productRequest.getPrice(), productRequest.getDiscountedPercent()));
        oldProduct.setQuantity(this.calculateQuantity(productRequest));
        oldProduct.setBrandProduct(brand);
        oldProduct.setParentCategoryOfProduct(parentCategory);
        oldProduct.setChildCategoryOfProduct(childCategory);

        return productRepository.save(oldProduct);
    }

    @Override
    @Transactional
    public Response deleteProduct(Long id) throws CustomException {
        Product product = this.getById(id);
        productRepository.delete(product);

        Response response = new Response();
        response.setMessage("Delete product success !!!");
        response.setStatus(HttpStatus.OK.value());

        return response;
    }

    @Override
    @Transactional
    public Response deleteSomeProducts(List<Long> listIdProducts) throws CustomException {
        List<Product> lstProductDelete = productRepository.findAllById(listIdProducts);

        List<Long> lstIdProductDelete = lstProductDelete.stream().map(Product::getId).collect(Collectors.toList());

        List<Long> lstIdProductMiss = listIdProducts.stream().filter(id -> !lstIdProductDelete.contains(id)).collect(Collectors.toList());

        productRepository.deleteAll(lstProductDelete);

        String message = lstIdProductMiss.isEmpty() ? "Delete some products success !!!" :
                "Delete some products success, but have some products not found: " + lstIdProductMiss;

        Response response = new Response();
        response.setStatus(HttpStatus.OK.value());
        response.setMessage(message);

        return response;
    }

    @Override
    public ListProductsResponse filterProductsByAdmin(String name, Long brandId, Long parentCategoryId, Long childCategoryId, String color,
                                                      Integer discountedPercent, String createBy, String updateBy, String code, Double price, int pageIndex, int pageSize) throws CustomException {
        List<Product> productsFilter = productRepository.filterProductsByAdmin(name,
                brandId, parentCategoryId, childCategoryId, color, discountedPercent, createBy, updateBy, code, price);

        Pageable pageable = PageRequest.of(pageIndex - 1, pageSize);
        int startIndex = (int) pageable.getOffset();
        int endIndex = Math.min(startIndex + pageable.getPageSize(), productsFilter.size());

        ListProductsResponse listProductsResponse = new ListProductsResponse();
        listProductsResponse.setListProducts(productsFilter.subList(startIndex, endIndex));
        listProductsResponse.setTotalProduct((long) productsFilter.size());

        return listProductsResponse;
    }

    @Override
    public List<Product> getAllProduct() {
        return productRepository.findAll();
    }

    @Override
    public ListProductsResponse getTwelveNewestProducts() throws CustomException {
        List<Product> products = productRepository.findTop12ByOrderByIdDesc();

        ListProductsResponse listProductsResponse = new ListProductsResponse();
        listProductsResponse.setTotalProduct((long) products.size());
        listProductsResponse.setListProducts(products);

        return listProductsResponse;
    }

    @Override
    public ListProductsResponse getTwelveProductsLeastQuantity() {
        List<Product> products = productRepository.findTop12ByOrderByQuantityAsc();

        ListProductsResponse listProductsResponse = new ListProductsResponse();
        listProductsResponse.setTotalProduct((long) products.size());
        listProductsResponse.setListProducts(products);

        return listProductsResponse;
    }

    @Override
    public ListProductsResponse getTwelveProductsMostQuantity() {
        List<Product> products = productRepository.findTop12ByOrderByQuantityDesc();

        ListProductsResponse listProductsResponse = new ListProductsResponse();
        listProductsResponse.setTotalProduct((long) products.size());
        listProductsResponse.setListProducts(products);

        return listProductsResponse;
    }

    @Override
    public ListProductsResponse filterProducts(String name, Long brandId, Long parentCategoryId, Long childCategoryId, String color,
                                               Double minPrice, Double maxPrice, String sort, Boolean sale, int pageIndex, int pageSize) {
        List<Product> products = productRepository.filterProducts(name, brandId, parentCategoryId, childCategoryId, color, minPrice, maxPrice, sort);

        if (sale) {
            products = products.stream().filter(p -> p.getDiscountedPercent() > 0).collect(Collectors.toList());
        }

        Pageable pageable = PageRequest.of(pageIndex - 1, pageSize);
        int startIndex = (int) pageable.getOffset();
        int endIndex = Math.min(startIndex + pageable.getPageSize(), products.size());

        ListProductsResponse listProductsResponse = new ListProductsResponse();
        listProductsResponse.setListProducts(products.subList(startIndex, endIndex));
        listProductsResponse.setTotalProduct((long) products.size());

        return listProductsResponse;
    }

    @Override
    public Long getTheHighestPriceOfProduct() {
        return productRepository.getTheHighestPriceOfProduct();
    }

    @Override
    public ListProductsResponse getSimilarProductsByBrandId(Long brandId, Long productId) {
        List<Product> products = productRepository.findTop12ByBrandProductId(brandId, productId);

        ListProductsResponse listProductsResponse = new ListProductsResponse();
        listProductsResponse.setTotalProduct((long) products.size());
        listProductsResponse.setListProducts(products);
        return listProductsResponse;
    }

    @Override
    public List<QuantityByBrandResponse> countQuantityByBrand() {
        return productRepository.countQuantityByBand();
    }

    @Override
    public List<TopBestSellerResponse> topTenBestSeller() {
        return productRepository.topTenBestSeller();
    }

    @Override
    public long stock() {
        return productRepository.stock();
    }

    @Override
    public Set<Size> getSizesOfProduct(Long id) throws CustomException {
        Product product = this.getById(id);
        return product.getSizes();
    }
}
