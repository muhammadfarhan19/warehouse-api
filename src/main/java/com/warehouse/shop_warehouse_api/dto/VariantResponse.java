package com.warehouse.shop_warehouse_api.dto;

import com.warehouse.shop_warehouse_api.entity.Variant;

import java.math.BigDecimal;

public class VariantResponse {

    private Long id;
    private Long itemId;
    private String itemName;
    private String name;
    private BigDecimal price;
    private Integer stock;

    public VariantResponse() {
    }

    public VariantResponse(
            Long id,
            Long itemId,
            String itemName,
            String name,
            BigDecimal price,
            Integer stock
    ) {
        this.id = id;
        this.itemId = itemId;
        this.itemName = itemName;
        this.name = name;
        this.price = price;
        this.stock = stock;
    }

    public static VariantResponse from(Variant variant) {
        return new VariantResponse(
                variant.getId(),
                variant.getItem().getId(),
                variant.getItem().getName(),
                variant.getName(),
                variant.getPrice(),
                variant.getStock()
        );
    }

    public Long getId() {
        return id;
    }

    public Long getItemId() {
        return itemId;
    }

    public String getItemName() {
        return itemName;
    }

    public String getName() {
        return name;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public Integer getStock() {
        return stock;
    }
}