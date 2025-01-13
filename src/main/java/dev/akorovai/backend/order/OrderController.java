package dev.akorovai.backend.order;

import dev.akorovai.backend.order.dto.OrderResponse;
import dev.akorovai.backend.security.ResponseRecord;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

	private final OrderService orderService;

	@GetMapping
	public ResponseEntity<ResponseRecord> getMyOrders() {
		List<OrderResponse> orders = orderService.getOrdersForAuthenticatedUser();

		ResponseRecord record = ResponseRecord.builder()
				                        .code(HttpStatus.OK.value())
				                        .message(orders)
				                        .build();

		return ResponseEntity.ok(record);
	}
}