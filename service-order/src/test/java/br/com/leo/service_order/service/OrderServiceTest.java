package br.com.leo.service_order.service;

import br.com.leo.service_order.model.Order;
import br.com.leo.service_order.model.OrderItem;
import br.com.leo.service_order.repository.OrderRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.web.client.RestTemplate;

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
    private RestTemplate restTemplate;
    private ObjectMapper objectMapper;
    private OrderService orderService;

    @BeforeEach
    void setUp() throws Exception {
        orderRepository = mock(OrderRepository.class);
        restTemplate = mock(RestTemplate.class);
        objectMapper = new ObjectMapper();

        orderService = new OrderService(orderRepository, restTemplate, objectMapper);

        // Mockando a resposta da API externa para qualquer produto
        String productJson = """
                {
                    "id": 1,
                    "name": "Produto Teste",
                    "description": "Descrição teste",
                    "price": 10.00,
                    "stock": 100
                }
                """;
        when(restTemplate.getForObject(anyString(), eq(String.class), any(Long.class)))
                .thenReturn(productJson);
    }

    @Test
    void create_ShouldCalculateLineTotalsAndOrderTotal_AndSaveOrder() {
        // Arrange
        OrderItem item1 = new OrderItem();
        item1.setProductId(1L);
        item1.setQuantity(2);

        OrderItem item2 = new OrderItem();
        item2.setProductId(2L);
        item2.setQuantity(1);

        Order order = new Order();
        order.setItems(Arrays.asList(item1, item2));

        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Order savedOrder = orderService.create(order);

        // Assert
        assertThat(savedOrder.getItems().get(0).getLineTotal()).isEqualByComparingTo("20.00");
        assertThat(savedOrder.getItems().get(1).getLineTotal()).isEqualByComparingTo("10.00"); // preço do mock
        assertThat(savedOrder.getTotal()).isEqualByComparingTo("30.00");

        ArgumentCaptor<Order> captor = ArgumentCaptor.forClass(Order.class);
        verify(orderRepository).save(captor.capture());
        assertThat(captor.getValue().getTotal()).isEqualByComparingTo("30.00");
    }

    @Test
    void findById_ShouldReturnOrder_WhenExists() {
        Order order = new Order();
        order.setId(1L);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        Order found = orderService.findById(1L);

        assertThat(found).isEqualTo(order);
    }

    @Test
    void findById_ShouldThrowException_WhenNotFound() {
        when(orderRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> orderService.findById(99L));
    }

    @Test
    void findAll_ShouldReturnListOfOrders() {
        Order order1 = new Order();
        Order order2 = new Order();
        when(orderRepository.findAll()).thenReturn(List.of(order1, order2));

        List<Order> orders = orderService.findAll();

        assertThat(orders).hasSize(2).contains(order1, order2);
    }
}
