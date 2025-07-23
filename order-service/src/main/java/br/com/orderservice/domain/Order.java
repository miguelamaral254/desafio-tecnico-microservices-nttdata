package br.com.orderservice.domain;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class Order {
    private Long id;
    private List<OrderItemDetail> items;
    private BigDecimal totalPrice;
    private LocalDateTime orderDate;

    @Data
    public static class OrderItemDetail {
        private ProductDTO product;
        private int quantity;
    }
}
