package dev.akorovai.backend.order;

import com.github.javafaker.Faker;
import dev.akorovai.backend.address_info.response.AddressInfoResponse;
import dev.akorovai.backend.handler.order.NoOrdersFoundException;
import dev.akorovai.backend.order.request.CreateOrderRequest;
import dev.akorovai.backend.order.response.OrderResponse;
import dev.akorovai.backend.product.Product;
import dev.akorovai.backend.product.ProductRepository;
import dev.akorovai.backend.security.JwtService;
import dev.akorovai.backend.shopping_cart.ShoppingCartItem;
import dev.akorovai.backend.shopping_cart.ShoppingCartItemRepository;
import dev.akorovai.backend.user.User;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

	@Mock
	private OrderRepository orderRepository;

	@Mock
	private JwtService jwtService;

	@Mock
	private OrderMapper orderMapper;

	@Mock
	private ShoppingCartItemRepository shoppingCartItemRepository;

	@Mock
	private ProductRepository productRepository;

	@InjectMocks
	private OrderService orderService;





	@Test
	void testGetOrdersForAuthenticatedUser_Success() {
		// Arrange
		User user = new User();
		Order order = Order.builder()
				              .id(1L)
				              .user(user)
				              .totalPrice(100.0)
				              .status(OrderStatus.IN_PROGRESS)
				              .createdAt(LocalDateTime.now())
				              .paymentMethod(PaymentMethod.VISA)
				              .shippingPrice(10.0)
				              .shippingTime(LocalDateTime.now().plusDays(1))
				              .build();

		OrderResponse orderResponse = OrderResponse.builder()
				                              .id(1L)
				                              .totalPrice(100.0)
				                              .status(OrderStatus.IN_PROGRESS)
				                              .createdAt(LocalDateTime.now())
				                              .shippingPrice(10.0)
				                              .shippingTime(LocalDateTime.now().plusDays(1))
				                              .build();

		when(jwtService.getAuthenticatedUser()).thenReturn(user);
		when(orderRepository.findByUser(user)).thenReturn(List.of(order));
		when(orderMapper.toResponse(order)).thenReturn(orderResponse);

		// Act
		List<OrderResponse> result = orderService.getOrdersForAuthenticatedUser();

		// Assert
		assertThat(result).hasSize(1);
		assertThat(result.get(0)).isEqualTo(orderResponse);

		verify(jwtService, times(1)).getAuthenticatedUser();
		verify(orderRepository, times(1)).findByUser(user);
		verify(orderMapper, times(1)).toResponse(order);
	}

	@Test
	void testGetOrdersForAuthenticatedUser_NoOrdersFound() {
		// Arrange
		User user = new User();

		when(jwtService.getAuthenticatedUser()).thenReturn(user);
		when(orderRepository.findByUser(user)).thenReturn(Collections.emptyList());

		// Act & Assert
		assertThatThrownBy(() -> orderService.getOrdersForAuthenticatedUser())
				.isInstanceOf(NoOrdersFoundException.class)
				.hasMessage("No orders found for the authenticated user.");

		verify(jwtService, times(1)).getAuthenticatedUser();
		verify(orderRepository, times(1)).findByUser(user);
	}

	@Test
	void testCreateOrder_Success() {
		// Arrange
		User user = new User();
		Product product = Product.builder()
				                  .id(1L)
				                  .name("Test Product")
				                  .price(50.0)
				                  .amount(10)
				                  .build();

		ShoppingCartItem cartItem = ShoppingCartItem.builder()
				                            .id(1L)
				                            .user(user)
				                            .product(product)
				                            .quantity(2)
				                            .build();

		AddressInfoResponse addressInfo = AddressInfoResponse.builder()
				                                  .id(1L)
				                                  .fullName("John Doe")
				                                  .state("California")
				                                  .street("123 Main St")
				                                  .apartment("Apt 4B")
				                                  .postalCode("12345")
				                                  .city("Los Angeles")
				                                  .phone("123456789")
				                                  .userId(101L)
				                                  .build();

		LocalDateTime shippingTime = LocalDateTime.now().plusDays(1); // Define shippingTime as LocalDateTime

		CreateOrderRequest request = CreateOrderRequest.builder()
				                             .email("john.doe@example.com")
				                             .addressInfo(addressInfo)
				                             .shippingPrice(10.99)
				                             .shippingTime(shippingTime.toString()) // Use LocalDateTime here
				                             .paymentMethod("VISA")
				                             .build();

		Order order = Order.builder()
				              .id(1L)
				              .user(user)
				              .totalPrice(120.0)
				              .status(OrderStatus.IN_PROGRESS)
				              .createdAt(LocalDateTime.now())
				              .paymentMethod(PaymentMethod.VISA)
				              .shippingPrice(10.0)
				              .shippingTime(shippingTime) // Use the same LocalDateTime
				              .build();

		OrderResponse orderResponse = OrderResponse.builder()
				                              .id(1L)
				                              .totalPrice(120.0)
				                              .status(OrderStatus.IN_PROGRESS)
				                              .createdAt(LocalDateTime.now())
				                              .shippingPrice(10.0)
				                              .shippingTime(shippingTime) // Use the same LocalDateTime
				                              .build();

		when(jwtService.getAuthenticatedUser()).thenReturn(user);
		when(shoppingCartItemRepository.findByUser(user)).thenReturn(List.of(cartItem));
		when(orderRepository.save(any(Order.class))).thenReturn(order);
		when(orderMapper.toResponse(order)).thenReturn(orderResponse);

		// Act
		OrderResponse result = orderService.createOrder(request);

		// Assert
		assertThat(result).isEqualTo(orderResponse);

		verify(jwtService, times(1)).getAuthenticatedUser();
		verify(shoppingCartItemRepository, times(1)).findByUser(user);
		verify(orderRepository, times(1)).save(any(Order.class));
		verify(orderMapper, times(1)).toResponse(order);
		verify(productRepository, times(1)).save(product);
		verify(shoppingCartItemRepository, times(1)).delete(cartItem);
	}

	@Test
	void testCreateOrder_EmptyCart() {
		// Arrange
		User user = new User();
		AddressInfoResponse addressInfo = AddressInfoResponse.builder()
				                                  .id(1L)
				                                  .fullName("John Doe")
				                                  .state("California")
				                                  .street("123 Main St")
				                                  .apartment("Apt 4B")
				                                  .postalCode("12345")
				                                  .city("Los Angeles")
				                                  .phone("123456789")
				                                  .userId(101L)
				                                  .build();

		LocalDateTime shippingTime = LocalDateTime.now().plusDays(1); // Define shippingTime as LocalDateTime

		CreateOrderRequest request = CreateOrderRequest.builder()
				                             .email("john.doe@example.com")
				                             .addressInfo(addressInfo)
				                             .shippingPrice(10.99)
				                             .shippingTime(shippingTime.toString()) // Use LocalDateTime here
				                             .paymentMethod("Credit Card")
				                             .build();

		when(jwtService.getAuthenticatedUser()).thenReturn(user);
		when(shoppingCartItemRepository.findByUser(user)).thenReturn(Collections.emptyList());

		// Act & Assert
		assertThatThrownBy(() -> orderService.createOrder(request))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessage("Cannot create an order with an empty cart.");

		verify(jwtService, times(1)).getAuthenticatedUser();
		verify(shoppingCartItemRepository, times(1)).findByUser(user);
	}

	@Test
	void testCreateOrder_InsufficientStock() {
		// Arrange
		User user = new User();
		Product product = Product.builder()
				                  .id(1L)
				                  .name("Test Product")
				                  .price(50.0)
				                  .amount(1)
				                  .build();

		ShoppingCartItem cartItem = ShoppingCartItem.builder()
				                            .id(1L)
				                            .user(user)
				                            .product(product)
				                            .quantity(2)
				                            .build();

		AddressInfoResponse addressInfo = AddressInfoResponse.builder()
				                                  .id(1L)
				                                  .fullName("John Doe")
				                                  .state("California")
				                                  .street("123 Main St")
				                                  .apartment("Apt 4B")
				                                  .postalCode("12345")
				                                  .city("Los Angeles")
				                                  .phone("123456789")
				                                  .userId(101L)
				                                  .build();

		LocalDateTime shippingTime = LocalDateTime.now().plusDays(1);

		CreateOrderRequest request = CreateOrderRequest.builder()
				                             .email("john.doe@example.com")
				                             .addressInfo(addressInfo)
				                             .shippingPrice(10.99)
				                             .shippingTime(shippingTime.toString())
				                             .paymentMethod("VISA")
				                             .build();

		when(jwtService.getAuthenticatedUser()).thenReturn(user);
		when(shoppingCartItemRepository.findByUser(user)).thenReturn(List.of(cartItem));

		// Act & Assert
		assertThatThrownBy(() -> orderService.createOrder(request))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessageContaining("Insufficient stock for product");

		verify(jwtService, times(1)).getAuthenticatedUser();
		verify(shoppingCartItemRepository, times(1)).findByUser(user);
	}
	@Test
	void testMarkOrdersAsDelivered() {
		// Arrange
		Order order = Order.builder()
				              .id(1L)
				              .status(OrderStatus.IN_PROGRESS)
				              .shippingTime(LocalDateTime.now().minusDays(1))
				              .build();

		when(orderRepository.findByStatusAndShippingTimeBefore(eq(OrderStatus.IN_PROGRESS), any(LocalDateTime.class)))
				.thenReturn(List.of(order));

		// Act
		orderService.markOrdersAsDelivered();

		// Assert
		assertThat(order.getStatus()).isEqualTo(OrderStatus.DELIVERED);
		verify(orderRepository, times(1)).save(order);
	}
}