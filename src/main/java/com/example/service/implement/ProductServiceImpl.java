package com.example.service.implement;

import com.example.Entity.*;
import com.example.mapper.ProductMapper;
import com.example.repository.BrandRepository;
import com.example.repository.ChildCategoryRepository;
import com.example.repository.ParentCategoryRepository;
import com.example.repository.ProductRepository;
import com.example.request.ProductRequest;
import com.example.response.*;
import com.example.service.ProductService;
import com.example.util.MethodUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService {
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private BrandRepository brandRepository;
    @Autowired
    private ParentCategoryRepository parentCategoryRepository;
    @Autowired
    private ChildCategoryRepository childCategoryRepository;
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

    @Override
    @Transactional
    public Product createProduct(ProductRequest productRequest) throws ResponseError {
        Brand brand = brandRepository.findById(productRequest.getBrandId())
                .orElseThrow(() -> new ResponseError(
                        "Brand not found with id: " + productRequest.getBrandId(),
                        HttpStatus.NOT_FOUND.value()
                ));

        ParentCategory parentCategory = parentCategoryRepository.findByIdAndAndBrandId(productRequest.getParentCategoryId(), brand.getId())
                .orElseThrow(() -> new ResponseError(
                        new StringBuilder("Parent category with id ")
                                .append(productRequest.getParentCategoryId())
                                .append(" and have brand id ")
                                .append(brand.getId())
                                .append(" not exist !!!")
                                .toString(),
                        HttpStatus.NOT_FOUND.value()
                ));

        ChildCategory childCategory = childCategoryRepository.findByIdAndParentCategoryId(productRequest.getChildCategoryId(), parentCategory.getId())
                .orElseThrow(() -> new ResponseError(
                        new StringBuilder("Child category with id ")
                                .append(productRequest.getChildCategoryId())
                                .append(" and have parent category id ")
                                .append(parentCategory.getId())
                                .append(" not exist !!!")
                                .toString(),
                        HttpStatus.NOT_FOUND.value()
                ));

        String emailAdmin = methodUtils.getEmailFromTokenOfAdmin();

        int quantity = productRequest.getSizes().stream()
                .mapToInt(Size::getQuantity)
                .sum();
        long discountedPrice = Math.round(productRequest.getPrice() - ((double) productRequest.getDiscountedPercent() / 100) * productRequest.getPrice());

        Product product = new Product();
        productMapper.productRequestToProduct(productRequest, product);

        product.setCode(generateUniqueCode(brand.getName()));
        product.setCreatedBy(emailAdmin);
        product.setDiscountedPrice(discountedPrice);
        product.setQuantity(quantity);
        product.setBrandProduct(brand);
        product.setParentCategoryOfProduct(parentCategory);
        product.setChildCategoryOfProduct(childCategory);
        product.setColor(productRequest.getColor().toUpperCase());

        return productRepository.save(product);
    }

    @Override
    @Transactional
    public Product updateProduct(Long id, ProductRequest productRequest) throws ResponseError {
        Product oldProduct = productRepository.findById(id)
                .orElseThrow(() -> new ResponseError(
                        "Product not found with id: " + id,
                        HttpStatus.NOT_FOUND.value()
                ));

        Brand brand = brandRepository.findById(productRequest.getBrandId())
                .orElseThrow(() -> new ResponseError(
                        "Brand not found with id: " + productRequest.getBrandId(),
                        HttpStatus.NOT_FOUND.value()
                ));

        ParentCategory parentCategory = parentCategoryRepository.findByIdAndAndBrandId(productRequest.getParentCategoryId(), brand.getId())
                .orElseThrow(() -> new ResponseError(
                        new StringBuilder("Parent category with id ")
                                .append(productRequest.getParentCategoryId())
                                .append(" and have brand id ")
                                .append(brand.getId())
                                .append(" not exist !!!")
                                .toString(),
                        HttpStatus.NOT_FOUND.value()
                ));

        ChildCategory childCategory = childCategoryRepository.findByIdAndParentCategoryId(productRequest.getChildCategoryId(), parentCategory.getId())
                .orElseThrow(() -> new ResponseError(
                        new StringBuilder("Child category with id ")
                                .append(productRequest.getChildCategoryId())
                                .append(" and have parent category id ")
                                .append(parentCategory.getId())
                                .append(" not exist !!!")
                                .toString(),
                        HttpStatus.NOT_FOUND.value()
                ));

        String emailAdmin = methodUtils.getEmailFromTokenOfAdmin();

        int quantity = productRequest.getSizes().stream().mapToInt(Size::getQuantity).sum();
        long discountedPrice = Math.round(productRequest.getPrice() - ((double) productRequest.getDiscountedPercent() / 100) * productRequest.getPrice());
        productMapper.productRequestToProduct(productRequest, oldProduct);
        oldProduct.setUpdateBy(emailAdmin);
        oldProduct.setDiscountedPrice(discountedPrice);
        oldProduct.setQuantity(quantity);
        oldProduct.setBrandProduct(brand);
        oldProduct.setParentCategoryOfProduct(parentCategory);
        oldProduct.setChildCategoryOfProduct(childCategory);
        oldProduct.setColor(productRequest.getColor().toUpperCase());

        return productRepository.save(oldProduct);
    }

    @Override
    @Transactional
    public Response deleteProduct(Long id) throws ResponseError {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResponseError(
                        "Product not found with id: " + id,
                        HttpStatus.NOT_FOUND.value()
                ));
        ;
        productRepository.delete(product);

        Response response = new Response();
        response.setMessage("Delete product success !!!");
        response.setStatus(HttpStatus.OK.value());

        return response;
    }

    @Override
    @Transactional
    public Response deleteSomeProducts(List<Long> listIdProducts) throws ResponseError {
        List<Long> idsMiss = new ArrayList<>();

        listIdProducts.forEach(id -> {
            Optional<Product> product = productRepository.findById(id);
            if (product.isPresent()) {
                productRepository.delete(product.get());
            } else {
                idsMiss.add(id);
            }
        });

        String message = idsMiss.isEmpty() ? "Delete some products success !!!" :
                "Delete some products success, but have some product not found: " + idsMiss.toString();

        Response response = new Response();
        response.setStatus(HttpStatus.OK.value());
        response.setMessage(message);

        return response;
    }

    @Override
    public ListProductsResponse filterProductsByAdmin(String name, Long brandId, Long parentCategoryId, Long childCategoryId, String color,
                                                      Integer discountedPercent, String createBy, String updateBy, String code, Double price, int pageIndex, int pageSize) throws ResponseError {
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
    public ListProductsResponse getTwelveNewestProducts() throws ResponseError {
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
    public Product getDetailProduct(Long id) throws ResponseError {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResponseError(
                        "Product not found with id: " + id,
                        HttpStatus.NOT_FOUND.value()
                ));
        ;
        return product;
    }
}
