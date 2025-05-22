package com.beauty.cart.repository;

import com.beauty.cart.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Item, Long> {
    @Query("SELECT DISTINCT i FROM Item i JOIN FETCH i.user u WHERE u.email = :email ORDER BY i.id")
    List<Item> getAllItems(String email);

    @Modifying
    @Query("DELETE FROM Item i WHERE i.id = :id")
    int deleteItemById(Long id);

    @Query("SELECT i FROM Item  i WHERE i.id = :id")
    Optional<Item> getItemById(Long id);

    @Modifying
    @Query("UPDATE Item i SET i.quantity = :newQuantity WHERE i.id = :id")
    int updateItemById(Long id, int newQuantity);

    @Query("SELECT i.id FROM Item  i WHERE i.productCode = :productCode")
    Long getIdByCode(String productCode);

}
