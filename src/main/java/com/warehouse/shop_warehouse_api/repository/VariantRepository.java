package com.warehouse.shop_warehouse_api.repository;

import com.warehouse.shop_warehouse_api.entity.Variant;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface VariantRepository extends JpaRepository<Variant, Long> {

    @EntityGraph(attributePaths = "item")
    List<Variant> findByItem_Id(Long itemId);

    @EntityGraph(attributePaths = "item")
    Optional<Variant> findWithItemById(Long id);

    @Modifying
    @Query("""
        UPDATE Variant v
        SET v.stock = v.stock - :quantity
        WHERE v.id = :id
        AND v.stock >= :quantity
    """)
    int decreaseStock(
            @Param("id") Long id,
            @Param("quantity") Integer quantity
    );
}