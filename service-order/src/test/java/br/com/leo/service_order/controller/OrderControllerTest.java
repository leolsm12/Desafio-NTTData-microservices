package br.com.leo.service_order.controller;

import br.com.leo.service_order.model.Order;
import br.com.leo.service_order.service.OrderService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.net.URI;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrderController.class)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderService orderService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void create_ShouldReturnCreatedOrder() throws Exception {
        Order order = new Order();
        order.setId(1L);
        order.setTotal(new BigDecimal("100.00"));

        when(orderService.create(any(Order.class))).thenReturn(order);

        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(order)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/orders/1"))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.total", is(100.00)));
    }

    @Test
    void findById_ShouldReturnOrder() throws Exception {
        Order order = new Order();
        order.setId(1L);
        order.setTotal(new BigDecimal("50.00"));

        when(orderService.findById(1L)).thenReturn(order);

        mockMvc.perform(get("/orders/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.total", is(50.00)));
    }

    @Test
    void findAll_ShouldReturnListOfOrders() throws Exception {
        Order order1 = new Order();
        order1.setId(1L);
        order1.setTotal(new BigDecimal("10.00"));

        Order order2 = new Order();
        order2.setId(2L);
        order2.setTotal(new BigDecimal("20.00"));

        when(orderService.findAll()).thenReturn(List.of(order1, order2));

        mockMvc.perform(get("/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[1].id", is(2)));
    }
}