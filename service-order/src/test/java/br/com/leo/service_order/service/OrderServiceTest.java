package br.com.leo.service_order.service;

import br.com.leo.service_order.model.Order;
import br.com.leo.service_order.model.OrderItem;
import br.com.leo.service_order.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {

    @Mock
    OrderRepository orderRepository;

    @InjectMocks
    OrderService orderService;

    @Test
    void shouldCalculateTotalAndSaveOrder() {
        OrderItem item1 = OrderItem.builder()
                .productId(1L)
                .productNameSnapshot("Mouse Gamer")
                .priceAtOrder(new BigDecimal("25.00"))
                .quantity(2)
                .build();

        Order order = Order.builder().items(List.of(item1)).build();

        when(orderRepository.save(any(Order.class))).thenAnswer(i -> {
            Order saved = i.getArgument(0);
            saved.setId(1L);
            return saved;
        });

        Order saved = orderService.create(order);

        assertNotNull(saved.getId());
        assertEquals(new BigDecimal("50.00"), saved.getTotal());
        assertEquals(new BigDecimal("50.00"), saved.getItems().get(0).getLineTotal());
    }

    @Test
    void shouldFindOrderById() {
        Order order = Order.builder().id(1L).total(BigDecimal.TEN).build();
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        Order found = orderService.findById(1L);

        assertEquals(1L, found.getId());
        assertEquals(BigDecimal.TEN, found.getTotal());
    }
}
