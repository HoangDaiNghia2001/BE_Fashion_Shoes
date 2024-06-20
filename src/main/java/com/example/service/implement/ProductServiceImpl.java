package com.example.service.implement;

import com.example.Entity.*;
import com.example.config.JwtProvider;
import com.example.constant.CookieConstant;
import com.example.exception.CustomException;
import com.example.repository.BrandRepository;
import com.example.repository.ChildCategoryRepository;
import com.example.repository.ParentCategoryRepository;
import com.example.repository.ProductRepository;
import com.example.request.FilterProductsByAdminRequest;
import com.example.request.ProductRequest;
import com.example.response.ProductResponse;
import com.example.service.ProductService;
import jakarta.servlet.http.HttpServletRequest;
import org.hibernate.query.spi.Limit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService {
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private JwtProvider jwtProvider;
    @Autowired
    private HttpServletRequest request;
    @Autowired
    private UserServiceImpl userService;
    @Autowired
    private BrandRepository brandRepository;
    @Autowired
    private ParentCategoryRepository parentCategoryRepository;
    @Autowired
    private ChildCategoryRepository childCategoryRepository;

    @Override
    @Transactional
    public Product createProduct(ProductRequest productRequest) throws CustomException, IOException {
        Optional<Brand> checkBrand = brandRepository.findById(productRequest.getBrandId());

        if (checkBrand.isPresent()) {
            Optional<ParentCategory> checkParentCategory = parentCategoryRepository.findByIdAndAndBrandId(productRequest.getParentCategoryId(), productRequest.getBrandId());

            if (checkParentCategory.isPresent()) {
                Optional<ChildCategory> checkChildCategory = childCategoryRepository.findByIdAndParentCategoryId(productRequest.getChildCategoryId(), productRequest.getParentCategoryId());

                if (checkChildCategory.isPresent()) {
                    String token = jwtProvider.getTokenFromCookie(request, CookieConstant.JWT_COOKIE_ADMIN);
                    User admin = userService.findUserProfileByJwt(token);

                    int quantity = 0;

                    Product product = new Product();

                    product.setCreatedBy(admin.getEmail());
                    product.setTitle(productRequest.getTitle());
                    product.setDescription(productRequest.getDescription());
                    product.setDiscountedPercent(productRequest.getDiscountedPercent());
                    product.setDiscountedPrice(Math.round(productRequest.getPrice() - ((double) productRequest.getDiscountedPercent() / 100) * productRequest.getPrice()));
                    product.setName(productRequest.getName());
                    product.setMainImageBase64(productRequest.getMainImageBase64());
                    product.setPrice(productRequest.getPrice());
                    for (Size s : productRequest.getSizes()) {
                        quantity += s.getQuantity();
                    }
                    product.setQuantity(quantity);
                    product.setBrandProduct(checkBrand.get());
                    product.setParentCategoryOfProduct(checkParentCategory.get());
                    product.setChildCategoryOfProduct(checkChildCategory.get());
                    product.setSizes(productRequest.getSizes());
                    product.setColor(productRequest.getColor().toUpperCase());
                    product.setImageSecondaries(productRequest.getImageSecondaries());

                    return productRepository.save(product);
                } else {
                    throw new CustomException("Child category with id " + productRequest.getChildCategoryId() + " not exist !!!");
                }
            } else {
                throw new CustomException("Parent category with id " + productRequest.getParentCategoryId() + " not exist !!!");
            }
        } else {
            throw new CustomException("Brand not found with id: " + productRequest.getBrandId());
        }
    }

    @Override
    @Transactional
    public Product updateProduct(Long id, ProductRequest productRequest) throws CustomException, IOException {
        Optional<Product> oldProduct = productRepository.findById(id);

        if (oldProduct.isPresent()) {
            Optional<Brand> checkBrand = brandRepository.findById(productRequest.getBrandId());

            if (checkBrand.isPresent()) {
                Optional<ParentCategory> checkParentCategory = parentCategoryRepository.findByIdAndAndBrandId(productRequest.getParentCategoryId(), productRequest.getBrandId());

                if (checkParentCategory.isPresent()) {
                    Optional<ChildCategory> checkChildCategory = childCategoryRepository.findByIdAndParentCategoryId(productRequest.getChildCategoryId(), productRequest.getParentCategoryId());

                    if (checkChildCategory.isPresent()) {
                        String token = jwtProvider.getTokenFromCookie(request, CookieConstant.JWT_COOKIE_ADMIN);
                        User admin = userService.findUserProfileByJwt(token);

                        int quantity = 0;

                        oldProduct.get().setUpdateBy(admin.getEmail());
                        oldProduct.get().setTitle(productRequest.getTitle());
                        oldProduct.get().setDescription(productRequest.getDescription());
                        oldProduct.get().setDiscountedPercent(productRequest.getDiscountedPercent());
                        oldProduct.get().setDiscountedPrice(productRequest.getPrice() - ((double) productRequest.getDiscountedPercent() / 100) * productRequest.getPrice());
                        oldProduct.get().setMainImageBase64(productRequest.getMainImageBase64());
                        oldProduct.get().setName(productRequest.getName());
                        oldProduct.get().setPrice(productRequest.getPrice());
                        for (Size s : productRequest.getSizes()) {
                            quantity += s.getQuantity();
                        }
                        oldProduct.get().setQuantity(quantity);
                        oldProduct.get().setBrandProduct(checkBrand.get());
                        oldProduct.get().setParentCategoryOfProduct(checkParentCategory.get());
                        oldProduct.get().setChildCategoryOfProduct(checkChildCategory.get());
                        oldProduct.get().setSizes(productRequest.getSizes());
                        oldProduct.get().setColor(productRequest.getColor().toUpperCase());
                        oldProduct.get().setImageSecondaries(productRequest.getImageSecondaries());

                        return productRepository.save(oldProduct.get());
                    } else {
                        throw new CustomException("Child category not found with id: " + productRequest.getChildCategoryId());
                    }
                } else {
                    throw new CustomException("Parent category not found with id: " + productRequest.getParentCategoryId());
                }
            } else {
                throw new CustomException("Brand not found with id: " + productRequest.getBrandId());
            }
        } else {
            throw new CustomException("Product not found with id: " + id);
        }
    }

    @Override
    @Transactional
    public void deleteProduct(Long id) throws CustomException {
        Optional<Product> product = productRepository.findById(id);
        if (product.isPresent()) {
            productRepository.delete(product.get());
        } else {
            throw new CustomException("Product not found with id: " + id);
        }
    }

    @Override
    @Transactional
    public void deleteSomeProducts(List<Long> listIdProducts) throws CustomException {
        listIdProducts.forEach(id -> {
            try {
                deleteProduct(id);
            } catch (CustomException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public ProductResponse filterProductsByAdmin(String name, Long brandId, Long parentCategoryId, Long childCategoryId, String color,
                                                 Integer discountedPercent, String createBy, String updateBy,Long id, Double price, int pageIndex, int pageSize) throws CustomException {
        List<Product> productsFilter = productRepository.filterProductsByAdmin(name,
                brandId, parentCategoryId, childCategoryId,color, discountedPercent, createBy, updateBy,id,price);

        Pageable pageable = PageRequest.of(pageIndex - 1, pageSize);
        int startIndex = (int) pageable.getOffset();
        int endIndex = Math.min(startIndex + pageable.getPageSize(), productsFilter.size());

        ProductResponse productResponse = new ProductResponse();
        productResponse.setListProducts(productsFilter.subList(startIndex, endIndex));
        productResponse.setTotalProduct((long) productsFilter.size());

        return productResponse;
    }

    @Override
    public List<Product> getAllProduct() {
        return productRepository.findAll();
    }

    @Override
    public ProductResponse getTwelveNewestProducts() throws CustomException {
        List<Product> products = productRepository.findTop12ByOrderByIdDesc();

        ProductResponse productResponse = new ProductResponse();
        productResponse.setTotalProduct((long) products.size());
        productResponse.setListProducts(products);

        return productResponse;
    }

    @Override
    public ProductResponse getTwelveProductsLeastQuantity() {
        List<Product> products = productRepository.findTop12ByOrderByQuantityAsc();

        ProductResponse productResponse = new ProductResponse();
        productResponse.setTotalProduct((long) products.size());
        productResponse.setListProducts(products);

        return productResponse;
    }

    @Override
    public ProductResponse getTwelveProductsMostQuantity() {
        List<Product> products = productRepository.findTop12ByOrderByQuantityDesc();

        ProductResponse productResponse = new ProductResponse();
        productResponse.setTotalProduct((long) products.size());
        productResponse.setListProducts(products);

        return productResponse;
    }

    // not test

    @Override
    public ProductResponse filterProduct(String name, Long brandId, Long parentCategoryId, Long childCategoryId, String color,
                                         Double minPrice, Double maxPrice, String sort, Boolean sale, int pageIndex, int pageSize) {
        List<Product> products = productRepository.filterProducts(name, brandId, parentCategoryId, childCategoryId, color, minPrice, maxPrice, sort);

        if (sale) {
            products = products.stream().filter(p -> p.getDiscountedPercent() > 0).collect(Collectors.toList());
        }

        Pageable pageable = PageRequest.of(pageIndex - 1, pageSize);
        int startIndex = (int) pageable.getOffset();
        int endIndex = Math.min(startIndex + pageable.getPageSize(), products.size());

        ProductResponse productResponse = new ProductResponse();
        productResponse.setListProducts(products.subList(startIndex, endIndex));
        productResponse.setTotalProduct((long) products.size());

        return productResponse;
    }

    @Override
    public Long getTheHighestPriceOfProduct() {
        return productRepository.getTheHighestPriceOfProduct();
    }

    @Override
    public ProductResponse getSimilarProductsByBrandId(Long brandId,Long productId) {
        List<Product>  products = productRepository.findTop12ByBrandProductId(brandId,productId);

        ProductResponse productResponse = new ProductResponse();
        productResponse.setTotalProduct((long) products.size());
        productResponse.setListProducts(products);
        return productResponse;
    }

    @Override
    public Product getDetailProduct(Long id) throws CustomException {
        Optional<Product> product = productRepository.findById(id);
        return product.orElseThrow(() -> new CustomException("Product not found with id: " + id));
    }

    @Override
    public String getMainImageBas64(Long idProduct) throws CustomException {
        return productRepository.getMainImageBase64(idProduct);
    }

    @Override
    public List<String> getSecondaryImagesBase64(Long idProduct) throws CustomException {
        return productRepository.getSecondaryImagesBase64(idProduct);
    }
}
