package com.warehouse.shop_warehouse_api.service;

import com.warehouse.shop_warehouse_api.entity.Item;
import com.warehouse.shop_warehouse_api.entity.Variant;
import com.warehouse.shop_warehouse_api.exception.ResourceNotFoundException;
import com.warehouse.shop_warehouse_api.repository.ItemRepository;
import com.warehouse.shop_warehouse_api.repository.VariantRepository;
import org.springframework.stereotype.Service;
import com.warehouse.shop_warehouse_api.exception.InsufficientStockException;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class VariantService {

    private final VariantRepository variantRepository;
    private final ItemRepository itemRepository;

    public VariantService(
            VariantRepository variantRepository,
            ItemRepository itemRepository
    ) {
        this.variantRepository = variantRepository;
        this.itemRepository = itemRepository;
    }

    public List<Variant> getByItemId(Long itemId) {
        if (!itemRepository.existsById(itemId)) {
            throw new ResourceNotFoundException("Item not found");
        }

        return variantRepository.findByItem_Id(itemId);
    }

   public Variant getById(Long id) {
        return variantRepository.findWithItemById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Variant not found")
                );
    }

    public Variant create(
            Long itemId,
            String name,
            BigDecimal price,
            Integer stock
    ) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Item not found")
                );

        Variant variant = new Variant();

        variant.setItem(item);
        variant.setName(name);
        variant.setPrice(price);
        variant.setStock(stock);

        return variantRepository.save(variant);
    }

    @Transactional
    public Variant update(
            Long id,
            String name,
            BigDecimal price,
            Integer stock
    ) {
        Variant variant = getById(id);

        variant.setName(name);
        variant.setPrice(price);
        variant.setStock(stock);

        return variantRepository.save(variant);
    }

    public void delete(Long id) {
        Variant variant = getById(id);

        variantRepository.delete(variant);
    }

    @Transactional
public Variant addStock(Long id, Integer quantity) {
    Variant variant = getById(id);

    variant.setStock(variant.getStock() + quantity);

    return variantRepository.save(variant);
}

@Transactional
public Variant sell(Long id, Integer quantity) {
    if (!variantRepository.existsById(id)) {
        throw new ResourceNotFoundException("Variant not found");
    }

    int updatedRows = variantRepository.decreaseStock(id, quantity);

    if (updatedRows == 0) {
        throw new InsufficientStockException(
                "Insufficient stock"
        );
    }

    return getById(id);
}


}