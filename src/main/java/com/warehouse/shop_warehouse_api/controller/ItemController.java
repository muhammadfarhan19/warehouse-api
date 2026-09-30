package com.warehouse.shop_warehouse_api.controller;

import com.warehouse.shop_warehouse_api.dto.ItemRequest;
import com.warehouse.shop_warehouse_api.entity.Item;
import com.warehouse.shop_warehouse_api.service.ItemService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/items")
public class ItemController {

    private final ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @GetMapping
    public ResponseEntity<List<Item>> getAll() {
        return ResponseEntity.ok(itemService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Item> getById(@PathVariable Long id) {
        return ResponseEntity.ok(itemService.getById(id));
    }

    @PostMapping
    public ResponseEntity<Item> create(
            @Valid @RequestBody ItemRequest request
    ) {
        Item item = itemService.create(request.getName());

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(item);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Item> update(
            @PathVariable Long id,
            @Valid @RequestBody ItemRequest request
    ) {
        Item item = itemService.update(id, request.getName());

        return ResponseEntity.ok(item);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        itemService.delete(id);

        return ResponseEntity.noContent().build();
    }
}