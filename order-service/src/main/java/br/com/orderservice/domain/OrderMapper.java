package br.com.orderservice.domain;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class OrderMapper {
    public OrderResponseDTO toResponseDto(Order order) {
        List<OrderResponseDTO.OrderItemDetail> itemDetails = order.getItems().stream()
                .map(item -> new OrderResponseDTO.OrderItemDetail(item.getProduct(), item.getQuantity()))
                .collect(Collectors.toList());

        return new OrderResponseDTO(order.getId(), itemDetails, order.getTotalPrice(), order.getOrderDate());
    }
}