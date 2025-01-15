package dev.akorovai.backend.order;

import dev.akorovai.backend.order.request.CreateOrderRequest;
import dev.akorovai.backend.order.response.OrderResponse;
import dev.akorovai.backend.security.ResponseRecord;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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


	@PostMapping
	public ResponseEntity<ResponseRecord> createOrder(@Valid @RequestBody CreateOrderRequest request) {
		OrderResponse orderResponse = orderService.createOrder(request);

		ResponseRecord record = ResponseRecord.builder()
				                        .code(HttpStatus.CREATED.value())
				                        .message(orderResponse)
				                        .build();

		return ResponseEntity.status(HttpStatus.CREATED).body(record);
	}
}