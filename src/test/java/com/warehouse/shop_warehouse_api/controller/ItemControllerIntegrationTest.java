package com.warehouse.shop_warehouse_api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.warehouse.shop_warehouse_api.dto.ItemRequest;
import com.warehouse.shop_warehouse_api.entity.Item;
import com.warehouse.shop_warehouse_api.repository.ItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import com.warehouse.shop_warehouse_api.repository.VariantRepository;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ItemControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private VariantRepository variantRepository;

    private Item item;
    
    @BeforeEach
    void setUp() {
        variantRepository.deleteAll();
        variantRepository.flush();

        itemRepository.deleteAll();
        itemRepository.flush();

        item = new Item("T-Shirt");
        item = itemRepository.save(item);
    }

    @Test
    void shouldCreateItem() throws Exception {
        ItemRequest request = new ItemRequest();
        request.setName("T-Shirt");

        mockMvc.perform(
                post("/api/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        )
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.name").value("T-Shirt"));
    }

    @Test
    void shouldGetItemById() throws Exception {
        Item item = itemRepository.save(new Item("T-Shirt"));

        mockMvc.perform(
                get("/api/items/{id}", item.getId())
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(item.getId()))
        .andExpect(jsonPath("$.name").value("T-Shirt"));
    }

    @Test
    void shouldUpdateItem() throws Exception {
        Item item = itemRepository.save(new Item("T-Shirt"));

        ItemRequest request = new ItemRequest();
        request.setName("Polo Shirt");

        mockMvc.perform(
                put("/api/items/{id}", item.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value("Polo Shirt"));
    }

    @Test
    void shouldDeleteItem() throws Exception {
        Item item = itemRepository.save(new Item("T-Shirt"));

        mockMvc.perform(
                delete("/api/items/{id}", item.getId())
        )
        .andExpect(status().isNoContent());

        mockMvc.perform(
                get("/api/items/{id}", item.getId())
        )
        .andExpect(status().isNotFound());
    }

    

        @Test
        void shouldReturn400WhenItemNameIsBlank() throws Exception {
        mockMvc.perform(
                post("/api/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                        "name": ""
                                }
                                """)
        )
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").value("Validation failed"))
        .andExpect(jsonPath("$.errors.name").exists());
        }
}