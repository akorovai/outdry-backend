package dev.akorovai.backend.order.dto;


import dev.akorovai.backend.order.OrderStatus;
import dev.akorovai.backend.orderItem.dto.OrderItemResponse;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderResponse {
	private Long id;
	private LocalDateTime shippingTime;
	private LocalDateTime createdAt;
	private Double totalPrice;
	private OrderStatus status;
	private Double shippingPrice;
	private Set<OrderItemResponse> orderItems;
}