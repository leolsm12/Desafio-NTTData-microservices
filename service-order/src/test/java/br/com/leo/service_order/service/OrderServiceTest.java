package br.com.leo.service_order.service;

import br.com.leo.service_order.model.Order;
import br.com.leo.service_order.model.OrderItem;
import br.com.leo.service_order.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class OrderServiceTest {

    private OrderRepository orderRepository;
    private OrderService orderService;

    @BeforeEach
    void setUp() {
        orderRepository = mock(OrderRepository.class);
        orderService = new OrderService(orderRepository);
    }

    @Test
    void create_ShouldCalculateLineTotalsAndOrderTotal_AndSaveOrder() {
        // Arrange
        OrderItem item1 = new OrderItem();
        item1.setPriceAtOrder(new BigDecimal("10.00"));
        item1.setQuantity(2);

        OrderItem item2 = new OrderItem();
        item2.setPriceAtOrder(new BigDecimal("5.50"));
        item2.setQuantity(1);

        Order order = new Order();
        order.setItems(Arrays.asList(item1, item2));

        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Order savedOrder = orderService.create(order);

        // Assert
        assertThat(savedOrder.getItems().get(0).getLineTotal()).isEqualByComparingTo("20.00");
        assertThat(savedOrder.getItems().get(1).getLineTotal()).isEqualByComparingTo("5.50");
        assertThat(savedOrder.getTotal()).isEqualByComparingTo("25.50");

        ArgumentCaptor<Order> captor = ArgumentCaptor.forClass(Order.class);
        verify(orderRepository).save(captor.capture());
        assertThat(captor.getValue().getTotal()).isEqualByComparingTo("25.50");
    }

    @Test
    void findById_ShouldReturnOrder_WhenExists() {
        // Arrange
        Order order = new Order();
        order.setId(1L);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        // Act
        Order found = orderService.findById(1L);

        // Assert
        assertThat(found).isEqualTo(order);
    }

    @Test
    void findById_ShouldThrowException_WhenNotFound() {
        // Arrange
        when(orderRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> orderService.findById(99L));
    }

    @Test
    void findAll_ShouldReturnListOfOrders() {
        // Arrange
        Order order1 = new Order();
        Order order2 = new Order();
        when(orderRepository.findAll()).thenReturn(List.of(order1, order2));

        // Act
        List<Order> orders = orderService.findAll();

        // Assert
        assertThat(orders).hasSize(2).contains(order1, order2);
    }
}