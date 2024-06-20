package com.example.repository;

import com.example.Entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query("select p from Product p where (?1 is null or p.name like %?1%) and" +
            "(?2 is null or p.brandProduct.id = ?2) and " +
            "(?3 is null or p.parentCategoryOfProduct.id = ?3) and " +
            "(?4 is null or p.childCategoryOfProduct.id = ?4) and " +
            "(?5 is null or p.color = ?5) and" +
            "(?6 is null and ?7 is null or p.discountedPrice between ?6 and ?7)" +
            "order by " +
            "case when ?8 = 'price_low' then p.discountedPrice end asc," +
            "case when ?8 = 'price_hight' then p.discountedPrice end desc," +
            "case when ?8 = 'newest' then p.id end desc")
    List<Product> filterProducts(String name, Long brandId, Long parentCategoryId, Long childCategoryId, String color,
                                 Double minPrice, Double maxPrice, String sort);

    @Query("select p from Product p where (?1 is null or p.name like %?1%) " +
            "and (?2 is null or p.brandProduct.id = ?2)" +
            "and (?3 is null or p.parentCategoryOfProduct.id = ?3)" +
            "and (?4 is null or p.childCategoryOfProduct.id = ?4)" +
            "and (?5 is null or p.color = ?5)" +
            "and (?6 is null or p.discountedPercent between 0 and ?6)" +
            "and (?7 is null or p.createdBy = ?7)" +
            "and (?8 is null or p.updateBy = ?8)" +
            "and (?9 is null or p.id = ?9)" +
            "and (?10 is null or p.price between 0 and ?10)" +
            "order by p.id desc ")
    List<Product> filterProductsByAdmin(String name, Long brandId, Long parentCategoryId, Long childCategoryId,
                                        String color, Integer discountedPercent, String createBy, String updateBy,Long id, Double price);

    @Query("select p.mainImageBase64 from Product p where p.id=?1")
    String getMainImageBase64(Long id);

    @Query("select p.imageSecondaries from Product p where p.id =?1")
    List<String> getSecondaryImagesBase64(Long id);

    List<Product> findTop12ByOrderByIdDesc();

    List<Product> findTop12ByOrderByQuantityAsc();

    List<Product> findTop12ByOrderByQuantityDesc();

    @Query("select max (p.discountedPrice) from Product p ")
    Long getTheHighestPriceOfProduct();

    // Lấy ra sản phẩm có cùng brand nhưng không lấy thêm sản phẩm chính nữa
    @Query(value = "select p from Product p where p.brandProduct.id = ?1 and p.id <> ?2  order by p.id desc limit 12")
    List<Product> findTop12ByBrandProductId(Long brandId, Long productId);

    // not test

    @Query("select p from Product p where p.brandProduct.name = ?1 order by p.id desc")
    List<Product> getAllProductByBrandName(String brandName);

    @Query("select p from Product p where p.brandProduct.name = ?1 and p.parentCategoryOfProduct.name = ?2 order by p.id desc")
    List<Product> getAllProductByParentCategory(String brand, String parentCategory);

    @Query("select p from Product p where p.brandProduct.name = ?1 and p.parentCategoryOfProduct.name = ?2 and p.childCategoryOfProduct.name = ?3 order by p.id desc")
    List<Product> getAllProductByChildCategory(String brand, String parentCategory, String childCategory);

    Product findTop1ByOrderByIdDesc();

    @Query("select p from Product p where p.name like %?1% order by p.id desc")
    List<Product> getAllProductBySearch(String search);

    @Query("select count(*) from Product p where p.brandProduct.name = ?1")
    int countProductByBrandName(String brandName);

    @Query("select count(*) from Product p where p.brandProduct.name = ?1 and p.parentCategoryOfProduct = ?2")
    int countProductByBrandNameAndParentCategory(String brandName, String parentCategory);

    @Query("select count(*) from Product p where p.name like %?1%")
    int countProductBySearch(String search);
}
