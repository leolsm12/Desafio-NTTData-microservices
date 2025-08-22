package br.com.leo.service_order.controller;

import br.com.leo.service_order.model.Order;
import br.com.leo.service_order.model.OrderItem;
import br.com.leo.service_order.service.OrderService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

public class OrderControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    OrderService orderService;

    @Test
    void shouldCreateOrderAndReturn201() throws Exception {
        OrderItem item = OrderItem.builder()
                .productId(1L)
                .productNameSnapshot("Mouse Gamer")
                .priceAtOrder(new BigDecimal("25.00"))
                .quantity(2)
                .lineTotal(new BigDecimal("50.00"))
                .build();

        Order order = Order.builder()
                .id(1L)
                .items(List.of(item))
                .total(new BigDecimal("50.00"))
                .build();

        when(orderService.create(any(Order.class))).thenReturn(order);

        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(order)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/orders/1"))
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void shouldReturnOrderById() throws Exception {
        Order order = Order.builder()
                .id(1L)
                .total(new BigDecimal("100.00"))
                .build();

        when(orderService.findById(1L)).thenReturn(order);

        mockMvc.perform(get("/orders/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.total").value(100.00));
    }
}
