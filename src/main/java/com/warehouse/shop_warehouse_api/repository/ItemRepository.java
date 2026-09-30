package com.warehouse.shop_warehouse_api.repository;

import com.warehouse.shop_warehouse_api.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemRepository extends JpaRepository<Item, Long> {
}