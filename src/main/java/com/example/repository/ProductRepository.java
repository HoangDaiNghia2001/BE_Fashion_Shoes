package com.example.repository;

import com.example.Entity.Product;
import com.example.response.QuantityByBrandResponse;
import com.example.response.TopBestSellerResponse;
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
            "and (?9 is null or p.code = ?9)" +
            "and (?10 is null or p.price between 0 and ?10)" +
            "order by p.id desc ")
    List<Product> filterProductsByAdmin(String name, Long brandId, Long parentCategoryId, Long childCategoryId,
                                        String color, Integer discountedPercent, String createBy, String updateBy, String code, Double price);



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

    @Query(value = "select new com.example.response.QuantityByBrandResponse(b.name, IFNULL(sum(p.quantity), 0)) " +
            "from Brand b " +
            "left join Product p " +
            "on b.id = p.brandProduct.id " +
            "group by b.id")
    List<QuantityByBrandResponse> countQuantityByBand();

    @Query("select new com.example.response.TopBestSellerResponse(p.id, p.name,p.quantity,sum(o.totalPrice), sum(o.quantity)) " +
            "from OrderLine o " +
            "left join Product p " +
            "on o.product.id = p.id " +
            "group by o.product.id " +
            "order by sum(o.quantity) desc " +
            "limit 10")
    List<TopBestSellerResponse> topTenBestSeller();

    @Query("select sum(p.quantity) from Product p")
    long stock();

    Product findByCode(String code);
}
