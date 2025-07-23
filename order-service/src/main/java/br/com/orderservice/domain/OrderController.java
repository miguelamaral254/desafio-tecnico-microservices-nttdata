package br.com.orderservice.domain;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@RestController
@RequestMapping("/orders")
public class OrderController {
    private final OrderService orderService;
    private final OrderMapper orderMapper;
    private final ProductServiceClient productServiceClient;

    @PostMapping
    public ResponseEntity<Void> createOrder(@RequestBody List<OrderItemRequest> requestedItems) {
        Order savedOrder = orderService.createOrder(requestedItems);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest().path("/{id}")
                .buildAndExpand(savedOrder.getId()).toUri();
        return ResponseEntity.created(location).build();
    }

    @GetMapping
    public ResponseEntity<List<OrderResponseDTO>> getAllOrders() {
        List<OrderResponseDTO> orders = orderService.getAllOrders().stream()
                .map(orderMapper::toResponseDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponseDTO> getOrderById(@PathVariable Long id) {
        return orderService.findById(id)
                .map(orderMapper::toResponseDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/product/{productId}")
    public ResponseEntity<String> simulateOrder(@PathVariable Long productId) {
        System.out.println("Simulando pedido para o produto com ID: " + productId);
        try {
            ProductDTO product = productServiceClient.getProductById(productId);
            System.out.println("Produto encontrado: " + product.name());

            String responseMessage = "Pedido simulado com sucesso para o produto: " + product.name() + " (ID: " + product.id() + ")";
            return ResponseEntity.ok(responseMessage);
        } catch (Exception e) {
            return ResponseEntity.status(404).body("Produto com ID " + productId + " não encontrado.");
        }
    }

    @GetMapping("/available-products")
    public ResponseEntity<List<ProductDTO>> listAllProducts() {
        return ResponseEntity.ok(productServiceClient.getAllProducts());
    }
}