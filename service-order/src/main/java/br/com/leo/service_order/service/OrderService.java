package br.com.leo.service_order.service;

import br.com.leo.service_order.model.Order;
import br.com.leo.service_order.model.OrderItem;
import br.com.leo.service_order.model.ProductDTO;
import br.com.leo.service_order.repository.OrderRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public Order create(Order order) {
        List<OrderItem> finalItems = new ArrayList<>();

        for (OrderItem itemReq : order.getItems()) {
            try {
                // Chama API externa e já transforma em DTO
                ProductDTO product = restTemplate.getForObject(
                        "http://localhost:8080/products/{id}",
                        ProductDTO.class,
                        itemReq.getProductId()
                );

                // Debug
                System.out.println("Product ID: " + itemReq.getProductId());
                System.out.println("Produto retornado: " + product);

                if (product == null) {
                    throw new RuntimeException("Produto não encontrado: " + itemReq.getProductId());
                }

                // Valida quantidade e estoque
                if (itemReq.getQuantity() <= 0) {
                    throw new RuntimeException("Quantidade inválida para produto " + product.getName());
                }
                if (product.getQuantity() < itemReq.getQuantity()) {
                    throw new RuntimeException("Estoque insuficiente para produto " + product.getName());
                }

                // Calcula lineTotal
                BigDecimal lineTotal = product.getPrice()
                        .multiply(BigDecimal.valueOf(itemReq.getQuantity()));

                // Monta OrderItem
                OrderItem orderItem = OrderItem.builder()
                        .productId(itemReq.getProductId())
                        .productNameSnapshot(product.getName())
                        .priceAtOrder(product.getPrice())
                        .quantity(itemReq.getQuantity())
                        .lineTotal(lineTotal)
                        .build();

                finalItems.add(orderItem);

            } catch (Exception e) {
                throw new RuntimeException("Erro ao buscar produto " + itemReq.getProductId(), e);
            }
        }

        // Soma os lineTotals
        BigDecimal total = finalItems.stream()
                .map(OrderItem::getLineTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        order.setItems(finalItems);
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
