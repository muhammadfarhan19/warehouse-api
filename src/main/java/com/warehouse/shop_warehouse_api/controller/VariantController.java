package com.warehouse.shop_warehouse_api.controller;

import com.warehouse.shop_warehouse_api.dto.StockRequest;
import com.warehouse.shop_warehouse_api.dto.VariantRequest;
import com.warehouse.shop_warehouse_api.dto.VariantResponse;
import com.warehouse.shop_warehouse_api.entity.Variant;
import com.warehouse.shop_warehouse_api.service.VariantService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class VariantController {

    private final VariantService variantService;

    public VariantController(VariantService variantService) {
        this.variantService = variantService;
    }

    @GetMapping("/items/{itemId}/variants")
    public ResponseEntity<List<VariantResponse>> getByItemId(
            @PathVariable Long itemId
    ) {
        List<VariantResponse> response = variantService
                .getByItemId(itemId)
                .stream()
                .map(VariantResponse::from)
                .toList();

        return ResponseEntity.ok(response);
    }

    @PostMapping("/items/{itemId}/variants")
    public ResponseEntity<VariantResponse> create(
            @PathVariable Long itemId,
            @Valid @RequestBody VariantRequest request
    ) {
        Variant variant = variantService.create(
                itemId,
                request.getName(),
                request.getPrice(),
                request.getStock()
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(VariantResponse.from(variant));
    }

    @GetMapping("/variants/{id}")
    public ResponseEntity<VariantResponse> getById(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(
                VariantResponse.from(
                        variantService.getById(id)
                )
        );
    }

    @PutMapping("/variants/{id}")
    public ResponseEntity<VariantResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody VariantRequest request
    ) {
        Variant variant = variantService.update(
                id,
                request.getName(),
                request.getPrice(),
                request.getStock()
        );

        return ResponseEntity.ok(
                VariantResponse.from(variant)
        );
    }

    @DeleteMapping("/variants/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id
    ) {
        variantService.delete(id);

        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/variants/{id}/stock")
    public ResponseEntity<VariantResponse> addStock(
            @PathVariable Long id,
            @Valid @RequestBody StockRequest request
    ) {
        Variant variant = variantService.addStock(
                id,
                request.getQuantity()
        );

        return ResponseEntity.ok(
                VariantResponse.from(variant)
        );
    }

    @PostMapping("/variants/{id}/sell")
    public ResponseEntity<VariantResponse> sell(
            @PathVariable Long id,
            @Valid @RequestBody StockRequest request
    ) {
        Variant variant = variantService.sell(
                id,
                request.getQuantity()
        );

        return ResponseEntity.ok(
                VariantResponse.from(variant)
        );
    }
}