package br.com.orderservice.domain;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductServiceClient productServiceClient;

    public Order createOrder(List<OrderItemRequest> requestedItems) {
        validateRequestedItems(requestedItems);

        List<Order.OrderItemDetail> orderItems = buildOrderItems(requestedItems);
        BigDecimal totalPrice = calculateTotalPrice(orderItems);

        Order newOrder = new Order();
        newOrder.setItems(orderItems);
        newOrder.setTotalPrice(totalPrice);
        newOrder.setOrderDate(LocalDateTime.now());

        return orderRepository.save(newOrder);
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public Optional<Order> findById(Long id) {
        return orderRepository.findById(id);
    }

    private void validateRequestedItems(List<OrderItemRequest> requestedItems) {
        if (requestedItems == null || requestedItems.isEmpty()) {
            throw new IllegalArgumentException("Precisa ser adicionado pelo menos 1 item");
        }

        requestedItems.forEach(item -> {
            if (item.quantity() <= 0) {
                throw new IllegalArgumentException("A quantidade para o produto ID " + item.productId() + " deve ser maior que zero.");
            }
        });
    }

    private List<Order.OrderItemDetail> buildOrderItems(List<OrderItemRequest> requestedItems) {
        return requestedItems.stream().map(req -> {
            ProductDTO product = productServiceClient.getProductById(req.productId());
            Order.OrderItemDetail itemDetail = new Order.OrderItemDetail();
            itemDetail.setProduct(product);
            itemDetail.setQuantity(req.quantity());
            return itemDetail;
        }).collect(Collectors.toList());
    }

    private BigDecimal calculateTotalPrice(List<Order.OrderItemDetail> items) {
        return items.stream()
                .map(item -> item.getProduct().price().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
