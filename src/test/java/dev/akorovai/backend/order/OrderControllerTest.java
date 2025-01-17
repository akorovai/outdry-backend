package dev.akorovai.backend.order;

import com.fasterxml.jackson.core.type.TypeReference;
import com.github.javafaker.Faker;
import dev.akorovai.backend.address_info.response.AddressInfoResponse;
import dev.akorovai.backend.order.request.CreateOrderRequest;
import dev.akorovai.backend.order.response.OrderResponse;
import dev.akorovai.backend.orderItem.dto.OrderItemResponse;
import dev.akorovai.backend.security.JsonUtils;
import dev.akorovai.backend.security.ResponseRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class OrderControllerTest {

	@Mock
	private OrderService orderService;

	@InjectMocks
	private OrderController orderController;

	private MockMvc mockMvc;
	private Faker faker;

	@BeforeEach
	void setUp() {
		mockMvc = MockMvcBuilders.standaloneSetup(orderController).build();
		faker = new Faker();
	}

	@Test
	void getMyOrders_ShouldReturnListOfOrders() throws Exception {
		// Arrange
		OrderItemResponse orderItemResponse = OrderItemResponse.builder()
				                                      .id(faker.number().randomNumber())
				                                      .quantity(faker.number().numberBetween(1, 10))
				                                      .price(faker.number().randomDouble(2, 10, 100))
				                                      .productId(faker.number().randomNumber())
				                                      .productName(faker.commerce().productName())
				                                      .size(faker.options().option("S", "M", "L", "XL"))
				                                      .color(faker.color().name())
				                                      .imageLink(faker.internet().image())
				                                      .build();

		OrderResponse orderResponse = OrderResponse.builder()
				                              .id(faker.number().randomNumber())
				                              .shippingTime(LocalDateTime.now().plusDays(2))
				                              .createdAt(LocalDateTime.now())
				                              .totalPrice(faker.number().randomDouble(2, 100, 500))
				                              .status(OrderStatus.IN_PROGRESS)
				                              .shippingPrice(faker.number().randomDouble(2, 5, 20))
				                              .orderItems(Collections.singleton(orderItemResponse))
				                              .build();

		when(orderService.getOrdersForAuthenticatedUser()).thenReturn(Collections.singletonList(orderResponse));

		// Act & Assert
		mockMvc.perform(get("/api/orders")
				                .contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.code", is(HttpStatus.OK.value())))
				.andExpect(jsonPath("$.message", hasSize(1)))
				.andExpect(jsonPath("$.message[0].id", is(orderResponse.getId().intValue())))
				.andExpect(jsonPath("$.message[0].shippingTime", notNullValue()))
				.andExpect(jsonPath("$.message[0].createdAt", notNullValue()))
				.andExpect(jsonPath("$.message[0].totalPrice", is(orderResponse.getTotalPrice())))
				.andExpect(jsonPath("$.message[0].status", is(orderResponse.getStatus().name())))
				.andExpect(jsonPath("$.message[0].shippingPrice", is(orderResponse.getShippingPrice())))
				.andExpect(jsonPath("$.message[0].orderItems", hasSize(1)))
				.andExpect(jsonPath("$.message[0].orderItems[0].id", is(orderItemResponse.getId().intValue())))
				.andExpect(jsonPath("$.message[0].orderItems[0].quantity", is(orderItemResponse.getQuantity())))
				.andExpect(jsonPath("$.message[0].orderItems[0].price", is(orderItemResponse.getPrice())))
				.andExpect(jsonPath("$.message[0].orderItems[0].productId", is(orderItemResponse.getProductId().intValue())))
				.andExpect(jsonPath("$.message[0].orderItems[0].productName", is(orderItemResponse.getProductName())))
				.andExpect(jsonPath("$.message[0].orderItems[0].size", is(orderItemResponse.getSize())))
				.andExpect(jsonPath("$.message[0].orderItems[0].color", is(orderItemResponse.getColor())))
				.andExpect(jsonPath("$.message[0].orderItems[0].imageLink", is(orderItemResponse.getImageLink())));

		// AssertJ assertions
		mockMvc.perform(get("/api/orders")
				                .contentType(MediaType.APPLICATION_JSON))
				.andDo(result -> {
					String jsonResponse = result.getResponse().getContentAsString();
					ResponseRecord responseRecord = JsonUtils.fromJson(jsonResponse, ResponseRecord.class);
					assertThat(responseRecord.getCode()).isEqualTo(HttpStatus.OK.value());

					// Deserialize the message field to a list of OrderResponse
					List<OrderResponse> responseOrders = JsonUtils.fromJson(
							JsonUtils.toJson(responseRecord.getMessage()),
							new TypeReference<>() {}
					);

					assertThat(responseOrders).hasSize(1);

					// Compare the deserialized OrderResponse with the expected one
					OrderResponse responseOrder = responseOrders.get(0);
					assertThat(responseOrder.getId()).isEqualTo(orderResponse.getId());
					assertThat(responseOrder.getShippingTime()).isEqualToIgnoringNanos(orderResponse.getShippingTime()); // Compare ignoring nanoseconds
					assertThat(responseOrder.getCreatedAt()).isEqualToIgnoringNanos(orderResponse.getCreatedAt()); // Compare ignoring nanoseconds
					assertThat(responseOrder.getTotalPrice()).isEqualTo(orderResponse.getTotalPrice());
					assertThat(responseOrder.getStatus()).isEqualTo(orderResponse.getStatus());
					assertThat(responseOrder.getShippingPrice()).isEqualTo(orderResponse.getShippingPrice());
					assertThat(responseOrder.getOrderItems()).hasSize(1);

					OrderItemResponse responseOrderItem = responseOrder.getOrderItems().iterator().next();
					assertThat(responseOrderItem.getId()).isEqualTo(orderItemResponse.getId());
					assertThat(responseOrderItem.getQuantity()).isEqualTo(orderItemResponse.getQuantity());
					assertThat(responseOrderItem.getPrice()).isEqualTo(orderItemResponse.getPrice());
					assertThat(responseOrderItem.getProductId()).isEqualTo(orderItemResponse.getProductId());
					assertThat(responseOrderItem.getProductName()).isEqualTo(orderItemResponse.getProductName());
					assertThat(responseOrderItem.getSize()).isEqualTo(orderItemResponse.getSize());
					assertThat(responseOrderItem.getColor()).isEqualTo(orderItemResponse.getColor());
					assertThat(responseOrderItem.getImageLink()).isEqualTo(orderItemResponse.getImageLink());
				});
	}

	@Test
	void createOrder_ShouldReturnCreatedOrder() throws Exception {
		// Arrange
		OrderItemResponse orderItemResponse = OrderItemResponse.builder()
				                                      .id(faker.number().randomNumber())
				                                      .quantity(faker.number().numberBetween(1, 10))
				                                      .price(faker.number().randomDouble(2, 10, 100))
				                                      .productId(faker.number().randomNumber())
				                                      .productName(faker.commerce().productName())
				                                      .size(faker.options().option("S", "M", "L", "XL"))
				                                      .color(faker.color().name())
				                                      .imageLink(faker.internet().image())
				                                      .build();

		AddressInfoResponse addressInfoResponse = AddressInfoResponse.builder()
				                                          .id(faker.number().randomNumber())
				                                          .fullName(faker.name().fullName())
				                                          .street(faker.address().streetAddress())
				                                          .state(faker.address().state())
				                                          .apartment(faker.company().name())
				                                          .city(faker.address().city())
				                                          .postalCode(faker.address().zipCode())
				                                          .phone(faker.phoneNumber().phoneNumber())
				                                          .userId(faker.number().randomNumber())
				                                          .build();

		CreateOrderRequest request = CreateOrderRequest.builder()
				                             .email(faker.internet().emailAddress())
				                             .addressInfo(addressInfoResponse)
				                             .shippingPrice(faker.number().randomDouble(2, 5, 20))
				                             .shippingTime(LocalDateTime.now().plusDays(2).toString())
				                             .paymentMethod(faker.options().option("CREDIT_CARD", "PAYPAL", "BANK_TRANSFER"))
				                             .build();

		OrderResponse orderResponse = OrderResponse.builder()
				                              .id(faker.number().randomNumber())
				                              .shippingTime(LocalDateTime.parse(request.getShippingTime()))
				                              .createdAt(LocalDateTime.now())
				                              .totalPrice(faker.number().randomDouble(2, 100, 500))
				                              .status(OrderStatus.IN_PROGRESS)
				                              .shippingPrice(request.getShippingPrice())
				                              .orderItems(Collections.singleton(orderItemResponse))
				                              .build();

		when(orderService.createOrder(any(CreateOrderRequest.class))).thenReturn(orderResponse);

		// Act & Assert
		mockMvc.perform(post("/api/orders")
				                .contentType(MediaType.APPLICATION_JSON)
				                .content(JsonUtils.toJson(request)))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.code", is(HttpStatus.CREATED.value())))
				.andExpect(jsonPath("$.message.id", is(orderResponse.getId().intValue())))
				.andExpect(jsonPath("$.message.shippingTime", notNullValue())) // Check that shippingTime is not null
				.andExpect(jsonPath("$.message.createdAt", notNullValue()))
				.andExpect(jsonPath("$.message.totalPrice", is(orderResponse.getTotalPrice())))
				.andExpect(jsonPath("$.message.status", is(orderResponse.getStatus().name())))
				.andExpect(jsonPath("$.message.shippingPrice", is(orderResponse.getShippingPrice())))
				.andExpect(jsonPath("$.message.orderItems", hasSize(1)))
				.andExpect(jsonPath("$.message.orderItems[0].id", is(orderItemResponse.getId().intValue())))
				.andExpect(jsonPath("$.message.orderItems[0].quantity", is(orderItemResponse.getQuantity())))
				.andExpect(jsonPath("$.message.orderItems[0].price", is(orderItemResponse.getPrice())))
				.andExpect(jsonPath("$.message.orderItems[0].productId", is(orderItemResponse.getProductId().intValue())))
				.andExpect(jsonPath("$.message.orderItems[0].productName", is(orderItemResponse.getProductName())))
				.andExpect(jsonPath("$.message.orderItems[0].size", is(orderItemResponse.getSize())))
				.andExpect(jsonPath("$.message.orderItems[0].color", is(orderItemResponse.getColor())))
				.andExpect(jsonPath("$.message.orderItems[0].imageLink", is(orderItemResponse.getImageLink())));

		// AssertJ assertions
		mockMvc.perform(post("/api/orders")
				                .contentType(MediaType.APPLICATION_JSON)
				                .content(JsonUtils.toJson(request)))
				.andDo(result -> {
					String jsonResponse = result.getResponse().getContentAsString();
					ResponseRecord responseRecord = JsonUtils.fromJson(jsonResponse, ResponseRecord.class);
					assertThat(responseRecord.getCode()).isEqualTo(HttpStatus.CREATED.value());

					// Deserialize the message field to an OrderResponse
					OrderResponse responseOrder = JsonUtils.fromJson(
							JsonUtils.toJson(responseRecord.getMessage()),
							OrderResponse.class
					);

					// Compare the deserialized OrderResponse with the expected one
					assertThat(responseOrder.getId()).isEqualTo(orderResponse.getId());
					assertThat(responseOrder.getShippingTime()).isEqualToIgnoringNanos(orderResponse.getShippingTime()); // Compare ignoring nanoseconds
					assertThat(responseOrder.getCreatedAt()).isEqualToIgnoringNanos(orderResponse.getCreatedAt()); // Compare ignoring nanoseconds
					assertThat(responseOrder.getTotalPrice()).isEqualTo(orderResponse.getTotalPrice());
					assertThat(responseOrder.getStatus()).isEqualTo(orderResponse.getStatus());
					assertThat(responseOrder.getShippingPrice()).isEqualTo(orderResponse.getShippingPrice());
					assertThat(responseOrder.getOrderItems()).hasSize(1);

					OrderItemResponse responseOrderItem = responseOrder.getOrderItems().iterator().next();
					assertThat(responseOrderItem.getId()).isEqualTo(orderItemResponse.getId());
					assertThat(responseOrderItem.getQuantity()).isEqualTo(orderItemResponse.getQuantity());
					assertThat(responseOrderItem.getPrice()).isEqualTo(orderItemResponse.getPrice());
					assertThat(responseOrderItem.getProductId()).isEqualTo(orderItemResponse.getProductId());
					assertThat(responseOrderItem.getProductName()).isEqualTo(orderItemResponse.getProductName());
					assertThat(responseOrderItem.getSize()).isEqualTo(orderItemResponse.getSize());
					assertThat(responseOrderItem.getColor()).isEqualTo(orderItemResponse.getColor());
					assertThat(responseOrderItem.getImageLink()).isEqualTo(orderItemResponse.getImageLink());
				});
	}
}