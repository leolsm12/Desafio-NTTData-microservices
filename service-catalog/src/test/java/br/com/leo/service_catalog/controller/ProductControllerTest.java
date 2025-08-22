package br.com.leo.service_catalog.controller;

import br.com.leo.service_catalog.model.Product;
import br.com.leo.service_catalog.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductController.class)
public class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @Autowired
    private ObjectMapper objectMapper;

    private Product product1;

    @BeforeEach
    void setUp() {
        product1 = new Product(1L, "Notebook Dell", "Dell Inspiron 15", 4500.0);
    }

    @Test
    void testGetAllProducts() throws Exception {
        when(productService.findAll()).thenReturn(Arrays.asList(product1));

        mockMvc.perform(get("/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Notebook Dell"));

        verify(productService, times(1)).findAll();
    }

    @Test
    void testGetProductById() throws Exception {
        when(productService.findById(1L)).thenReturn(Optional.of(product1));

        mockMvc.perform(get("/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Notebook Dell"));

        verify(productService, times(1)).findById(1L);
    }

    @Test
    void testCreateProduct() throws Exception {
        when(productService.save(any(Product.class))).thenReturn(product1);

        mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(product1)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Notebook Dell"));

        verify(productService, times(1)).save(any(Product.class));
    }

    @Test
    void testUpdateProduct() throws Exception {
        Product updatedProduct = new Product(null, "Notebook Dell Atualizado", "Descrição nova", 5000.0);
        when(productService.update(eq(1L), any(Product.class))).thenReturn(updatedProduct);

        mockMvc.perform(put("/products/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedProduct)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Notebook Dell Atualizado"));

        verify(productService, times(1)).update(eq(1L), any(Product.class));
    }

    @Test
    void testDeleteProduct() throws Exception {
        doNothing().when(productService).delete(1L);

        mockMvc.perform(delete("/products/1"))
                .andExpect(status().isOk());

        verify(productService, times(1)).delete(1L);
    }
}
