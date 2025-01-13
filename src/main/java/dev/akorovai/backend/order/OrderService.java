package dev.akorovai.backend.order;

import dev.akorovai.backend.handler.order.NoOrdersFoundException;
import dev.akorovai.backend.order.dto.OrderResponse;
import dev.akorovai.backend.security.JwtService;
import dev.akorovai.backend.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

	private final OrderRepository orderRepository;
	private final JwtService jwtService;
	private final OrderMapper orderMapper;

	public List<OrderResponse> getOrdersForAuthenticatedUser() {
		User user = jwtService.getAuthenticatedUser();
		List<Order> orders = orderRepository.findByUser(user);

		return Optional.ofNullable(orders)
				       .filter(list -> !list.isEmpty())
				       .map(list -> list.stream()
						                    .map(orderMapper::toResponse)
						                    .collect(Collectors.toList()))
				       .orElseThrow(() -> new NoOrdersFoundException("No orders found for the authenticated user."));
	}
}