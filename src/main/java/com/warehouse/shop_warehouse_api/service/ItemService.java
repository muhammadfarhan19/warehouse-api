package com.warehouse.shop_warehouse_api.service;

import com.warehouse.shop_warehouse_api.entity.Item;
import com.warehouse.shop_warehouse_api.exception.ResourceNotFoundException;
import com.warehouse.shop_warehouse_api.repository.ItemRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ItemService {

    private final ItemRepository itemRepository;

    public ItemService(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    public List<Item> getAll() {
        return itemRepository.findAll();
    }

    public Item getById(Long id) {
        return itemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Item not found"));
    }

    public Item create(String name) {
        Item item = new Item(name);

        return itemRepository.save(item);
    }

    public Item update(Long id, String name) {
        Item item = getById(id);

        item.setName(name);

        return itemRepository.save(item);
    }

    public void delete(Long id) {
        Item item = getById(id);

        itemRepository.delete(item);
    }
}