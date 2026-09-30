package com.warehouse.shop_warehouse_api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.warehouse.shop_warehouse_api.dto.StockRequest;
import com.warehouse.shop_warehouse_api.entity.Item;
import com.warehouse.shop_warehouse_api.entity.Variant;
import com.warehouse.shop_warehouse_api.repository.ItemRepository;
import com.warehouse.shop_warehouse_api.repository.VariantRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@SpringBootTest
@AutoConfigureMockMvc
class VariantControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private VariantRepository variantRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Variant variant;

    private Item item;

    @BeforeEach
    void setUp() {
        variantRepository.deleteAll();
        itemRepository.deleteAll();

        item = new Item("T-Shirt");
        item = itemRepository.save(item);

        variant = new Variant();
        variant.setItem(item);
        variant.setName("Black - M");
        variant.setPrice(new java.math.BigDecimal("150000"));
        variant.setStock(10);

        variant = variantRepository.save(variant);
    }

    @Test
    void shouldReturn409WhenSellingMoreThanStock() throws Exception {
        StockRequest request = new StockRequest();
        request.setQuantity(11);

        mockMvc.perform(
                post("/api/variants/{id}/sell", variant.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        )
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.message")
                .value("Insufficient stock"));
    }

    @Test
    void shouldReturn200WhenSellingWithinStock() throws Exception {
        StockRequest request = new StockRequest();
        request.setQuantity(3);

        mockMvc.perform(
                post("/api/variants/{id}/sell", variant.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.stock")
                .value(7));
    }

    @Test
        void shouldReturn400WhenSellQuantityIsInvalid() throws Exception {
        StockRequest request = new StockRequest();
        request.setQuantity(0);

        mockMvc.perform(
                post("/api/variants/{id}/sell", variant.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        )
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message")
                .value("Validation failed"))
        .andExpect(jsonPath("$.errors.quantity")
                .value("Quantity must be at least 1"));
        }

        @Test
        void shouldReturn404WhenVariantDoesNotExist() throws Exception {
        StockRequest request = new StockRequest();
        request.setQuantity(1);

        mockMvc.perform(
                post("/api/variants/{id}/sell", 99999L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        )
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.message")
                .value("Variant not found"));
        }

        @Test
        void shouldAddStock() throws Exception {
        mockMvc.perform(
                patch("/api/variants/" + variant.getId() + "/stock")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                        "quantity": 5
                                }
                                """)
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.stock").value(15));
        }

        @Test
        void shouldCreateVariant() throws Exception {
        mockMvc.perform(
                post("/api/items/" + item.getId() + "/variants")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                        "name": "White - L",
                                        "price": 175000,
                                        "stock": 20
                                }
                                """)
        )
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.name").value("White - L"))
        .andExpect(jsonPath("$.price").value(175000))
        .andExpect(jsonPath("$.stock").value(20))
        .andExpect(jsonPath("$.itemId").value(item.getId()));
        }

        @Test
        void shouldGetVariantsByItem() throws Exception {
        mockMvc.perform(
                get("/api/items/" + item.getId() + "/variants")
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].name").value("Black - M"))
        .andExpect(jsonPath("$[0].stock").value(10));
        }

        @Test
        void shouldUpdateVariant() throws Exception {
        mockMvc.perform(
                put("/api/variants/" + variant.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                        "name": "Black - L",
                                        "price": 160000,
                                        "stock": 8
                                }
                                """)
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value("Black - L"))
        .andExpect(jsonPath("$.price").value(160000))
        .andExpect(jsonPath("$.stock").value(8));
        }

}