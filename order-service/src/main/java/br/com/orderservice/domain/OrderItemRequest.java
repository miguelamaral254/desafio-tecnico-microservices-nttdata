package br.com.orderservice.domain;

public record OrderItemRequest(
        Long productId,
        int quantity
) {}