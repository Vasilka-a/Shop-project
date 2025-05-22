package com.beauty.product.repository;

import com.beauty.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query("SELECT p FROM Product p ORDER BY p.id")
    List<Product> getAllProducts();

    @Query("SELECT p FROM Product p WHERE p.productCode = :code")
    Optional<Product> getProductByCode(String code);

    @Query("SELECT p.quantity FROM Product p where p.productCode = :code")
    int getProductQuantityByCode(String code);

    @Modifying
    @Query("UPDATE Product p SET p.quantity = :newQuantity WHERE p.id = :id")
    int updateQuantityProduct(Long id, int newQuantity);

    @Modifying
    @Query("DELETE FROM Product p WHERE p.id = :id")
    int deleteProductById(Long id);

    @Modifying
    @Query("UPDATE Product p SET p.price = :newPrice WHERE p.id = :id")
    int updateProductPriceById(Long id, BigDecimal newPrice);


}
