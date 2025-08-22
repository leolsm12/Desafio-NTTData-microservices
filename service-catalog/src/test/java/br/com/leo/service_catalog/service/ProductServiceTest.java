package br.com.leo.service_catalog.service;

import br.com.leo.service_catalog.model.Product;
import br.com.leo.service_catalog.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

public class ProductServiceTest {

    @Mock
    private ProductRepository repository;

    @InjectMocks
    private ProductService service;

    private Product product1;
    private Product product2;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        product1 = new Product(1L, "Notebook Dell", "Dell Inspiron 15", 4500.0);
        product2 = new Product(2L, "Mouse Logitech", "Mouse sem fio", 150.0);
    }

    @Test
    void testFindAll() {
        when(repository.findAll()).thenReturn(Arrays.asList(product1, product2));

        List<Product> products = service.findAll();
        assertEquals(2, products.size());
        verify(repository, times(1)).findAll();
    }

    @Test
    void testFindById() {
        when(repository.findById(1L)).thenReturn(Optional.of(product1));

        Optional<Product> result = service.findById(1L);
        assertTrue(result.isPresent());
        assertEquals("Notebook Dell", result.get().getName());
        verify(repository, times(1)).findById(1L);
    }

    @Test
    void testSave() {
        when(repository.save(product1)).thenReturn(product1);

        Product saved = service.save(product1);
        assertNotNull(saved);
        assertEquals(product1.getName(), saved.getName());
        verify(repository, times(1)).save(product1);
    }

    @Test
    void testUpdate() {
        Product updatedProduct = new Product(null, "Notebook Dell Atualizado", "Descrição nova", 5000.0);

        when(repository.findById(1L)).thenReturn(Optional.of(product1));
        when(repository.save(any(Product.class))).thenReturn(updatedProduct);

        Product result = service.update(1L, updatedProduct);
        assertEquals("Notebook Dell Atualizado", result.getName());
        verify(repository, times(1)).findById(1L);
        verify(repository, times(1)).save(product1);
    }

    @Test
    void testDelete() {
        service.delete(1L);
        verify(repository, times(1)).deleteById(1L);
    }
}
