package br.com.leo.service_order.service;

import br.com.leo.service_order.model.Order;
import br.com.leo.service_order.model.OrderItem;
import br.com.leo.service_order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;

    public Order create(Order order) {
        // calcula lineTotal de cada item
        order.getItems().forEach(item -> {
            item.setLineTotal(item.getPriceAtOrder()
                    .multiply(BigDecimal.valueOf(item.getQuantity())));
        });

        // calcula total do pedido
        BigDecimal total = order.getItems().stream()
                .map(OrderItem::getLineTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        order.setTotal(total);

        return orderRepository.save(order);
    }

    public Order findById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));
    }

    public java.util.List<Order> findAll() {
        return orderRepository.findAll();
    }
}
