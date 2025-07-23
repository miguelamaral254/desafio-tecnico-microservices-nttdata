package br.com.orderservice.domain;

import org.springframework.stereotype.Repository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class OrderRepository {
    private final List<Order> orders = new ArrayList<>();
    private final AtomicLong counter = new AtomicLong();

    public Order save(Order order) {
        order.setId(counter.incrementAndGet());
        orders.add(order);
        return order;
    }

    public List<Order> findAll() {
        return new ArrayList<>(orders);
    }

    public Optional<Order> findById(Long id) {
        return orders.stream().filter(o -> o.getId().equals(id)).findFirst();
    }
}