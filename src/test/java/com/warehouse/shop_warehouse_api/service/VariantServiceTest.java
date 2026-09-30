package com.warehouse.shop_warehouse_api.service;

import com.warehouse.shop_warehouse_api.entity.Item;
import com.warehouse.shop_warehouse_api.entity.Variant;
import com.warehouse.shop_warehouse_api.exception.InsufficientStockException;
import com.warehouse.shop_warehouse_api.repository.ItemRepository;
import com.warehouse.shop_warehouse_api.repository.VariantRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VariantServiceTest {

    @Mock
    private VariantRepository variantRepository;

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private VariantService variantService;

    private Variant variant;

    @BeforeEach
    void setUp() {
        variant = new Variant();
        variant.setName("Black - M");
        variant.setPrice(new BigDecimal("150000"));
        variant.setStock(10);
    }

   @Test
    void shouldSellWhenStockIsSufficient() {
        when(variantRepository.existsById(1L))
                .thenReturn(true);

        when(variantRepository.decreaseStock(1L, 3))
                .thenAnswer(invocation -> {
                    variant.setStock(variant.getStock() - 3);
                    return 1;
                });

        when(variantRepository.findWithItemById(1L))
                .thenReturn(Optional.of(variant));

        Variant result = variantService.sell(1L, 3);

        assertEquals(7, result.getStock());

        verify(variantRepository)
                .decreaseStock(1L, 3);
    }

   @Test
    void shouldRejectSellWhenStockIsInsufficient() {
        when(variantRepository.existsById(1L))
                .thenReturn(true);

        when(variantRepository.decreaseStock(1L, 11))
                .thenReturn(0);

        InsufficientStockException exception = assertThrows(
                InsufficientStockException.class,
                () -> variantService.sell(1L, 11)
        );

        assertEquals(
                "Insufficient stock",
                exception.getMessage()
        );

        assertEquals(10, variant.getStock());

        verify(variantRepository)
                .decreaseStock(1L, 11);

        verify(variantRepository, never())
                .save(any(Variant.class));
    }

    @Test
    void shouldSellWhenQuantityEqualsStock() {
        when(variantRepository.existsById(1L))
                .thenReturn(true);

        when(variantRepository.decreaseStock(1L, 10))
                .thenAnswer(invocation -> {
                    variant.setStock(0);
                    return 1;
                });

        when(variantRepository.findWithItemById(1L))
                .thenReturn(Optional.of(variant));

        Variant result = variantService.sell(1L, 10);

        assertEquals(0, result.getStock());

        verify(variantRepository)
                .decreaseStock(1L, 10);
    }
}