package br.com.orderservice.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrderResponseDTO(
        Long orderId,
        List<OrderItemDetail> items,
        BigDecimal totalPrice,
        LocalDateTime orderDate
) {
    public record OrderItemDetail(
            ProductDTO product,
            int quantity
    ) {}
}