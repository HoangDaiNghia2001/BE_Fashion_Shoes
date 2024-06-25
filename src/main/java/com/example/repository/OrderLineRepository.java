package com.example.repository;

import com.example.Entity.OrderLine;
import com.example.Entity.Product;
import com.example.response.QuantityByBrandResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OrderLineRepository extends JpaRepository<OrderLine,Long> {
    List<OrderLine> findByOrderId(Long orderId);

    @Query("select new com.example.response.QuantityByBrandResponse(b.name, IFNULL(sum(o.quantity), 0)) " +
            "from Brand b " +
            "left join Product p " +
            "on b.id = p.brandProduct.id " +
            "left join OrderLine o " +
            "on p.id = o.product.id " +
            "group by b.id")
    List<QuantityByBrandResponse> quantityProductSoldByBrand();

    @Query("select sum(o.quantity) from OrderLine o")
    long sold();
}
