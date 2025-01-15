package dev.akorovai.backend.order.response;


import com.fasterxml.jackson.annotation.JsonFormat;
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

	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
	private LocalDateTime shippingTime;

	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
	private LocalDateTime createdAt;
	private Double totalPrice;
	private OrderStatus status;
	private Double shippingPrice;
	private Set<OrderItemResponse> orderItems;
}