package dev.akorovai.backend.orderItem;

import com.github.javafaker.Faker;
import dev.akorovai.backend.color.Color;
import dev.akorovai.backend.orderItem.dto.OrderItemResponse;
import dev.akorovai.backend.product.Product;

import dev.akorovai.backend.product.Size;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderItemMapperTest {

	@InjectMocks
	private OrderItemMapper orderItemMapper = OrderItemMapper.INSTANCE;

	private Faker faker;

	@BeforeEach
	void setUp() {
		faker = new Faker();
	}

	@Test
	void testToResponse() {
		// Arrange
		OrderItem orderItem = mock(OrderItem.class);
		Product product = mock(Product.class);
		Size size = mock(Size.class);
		Color color = mock(Color.class);

		when(orderItem.getProduct()).thenReturn(product);
		when(product.getId()).thenReturn(101L);
		when(product.getName()).thenReturn("Test Product");
		when(product.getSize()).thenReturn(size);
		when(size.getDisplayName()).thenReturn("Medium");
		when(product.getColor()).thenReturn(color);
		when(color.getName()).thenReturn("Red");
		when(product.getLinks()).thenReturn(List.of("link1", "link2"));

		// Act
		OrderItemResponse response = orderItemMapper.toResponse(orderItem);

		// Assert using AssertJ
		assertThat(response).isNotNull();
		assertThat(response.getProductId()).isEqualTo(101L);
		assertThat(response.getProductName()).isEqualTo("Test Product");
		assertThat(response.getSize()).isEqualTo("Medium");
		assertThat(response.getColor()).isEqualTo("Red");
		assertThat(response.getImageLink()).isEqualTo("link1");

		// Assert using Hamcrest
		assertThat(response, is(notNullValue()));
		assertThat(response.getProductId(), is(101L));
		assertThat(response.getProductName(), is("Test Product"));
		assertThat(response.getSize(), is("Medium"));
		assertThat(response.getColor(), is("Red"));
		assertThat(response.getImageLink(), is("link1"));
	}

	@Test
	void testToResponseSet() {
		// Arrange
		OrderItem orderItem1 = mock(OrderItem.class);
		OrderItem orderItem2 = mock(OrderItem.class);
		Product product1 = mock(Product.class);
		Product product2 = mock(Product.class);
		Size size1 = mock(Size.class);
		Size size2 = mock(Size.class);
		Color color1 = mock(Color.class);
		Color color2 = mock(Color.class);

		when(orderItem1.getProduct()).thenReturn(product1);
		when(product1.getId()).thenReturn(101L);
		when(product1.getName()).thenReturn("Product 1");
		when(product1.getSize()).thenReturn(size1);
		when(size1.getDisplayName()).thenReturn("Small");
		when(product1.getColor()).thenReturn(color1);
		when(color1.getName()).thenReturn("Blue");
		when(product1.getLinks()).thenReturn(List.of("link1"));

		when(orderItem2.getProduct()).thenReturn(product2);
		when(product2.getId()).thenReturn(102L);
		when(product2.getName()).thenReturn("Product 2");
		when(product2.getSize()).thenReturn(size2);
		when(size2.getDisplayName()).thenReturn("Large");
		when(product2.getColor()).thenReturn(color2);
		when(color2.getName()).thenReturn("Green");
		when(product2.getLinks()).thenReturn(List.of("link2"));

		Set<OrderItem> orderItems = Set.of(orderItem1, orderItem2);

		// Act
		Set<OrderItemResponse> responseSet = orderItemMapper.toResponseSet(orderItems);

		// Assert using AssertJ
		assertThat(responseSet).hasSize(2);
		assertThat(responseSet).extracting(OrderItemResponse::getProductId)
				.containsExactlyInAnyOrder(101L, 102L);
		assertThat(responseSet).extracting(OrderItemResponse::getProductName)
				.containsExactlyInAnyOrder("Product 1", "Product 2");
		assertThat(responseSet).extracting(OrderItemResponse::getSize)
				.containsExactlyInAnyOrder("Small", "Large");
		assertThat(responseSet).extracting(OrderItemResponse::getColor)
				.containsExactlyInAnyOrder("Blue", "Green");
		assertThat(responseSet).extracting(OrderItemResponse::getImageLink)
				.containsExactlyInAnyOrder("link1", "link2");

		// Assert using Hamcrest
		assertThat(responseSet, hasSize(2));
		assertThat(responseSet, containsInAnyOrder(
				allOf(
						hasProperty("productId", is(101L)),
						hasProperty("productName", is("Product 1")),
						hasProperty("size", is("Small")),
						hasProperty("color", is("Blue")),
						hasProperty("imageLink", is("link1"))
				),
				allOf(
						hasProperty("productId", is(102L)),
						hasProperty("productName", is("Product 2")),
						hasProperty("size", is("Large")),
						hasProperty("color", is("Green")),
						hasProperty("imageLink", is("link2"))
				)
		));
	}

	@Test
	void testMapFirstLink() {
		// Arrange
		List<String> links = List.of("link1", "link2");

		// Act
		String firstLink = orderItemMapper.mapFirstLink(links);

		// Assert using AssertJ
		assertThat(firstLink).isEqualTo("link1");

		// Assert using Hamcrest
		assertThat(firstLink, is("link1"));
	}

	@Test
	void testMapFirstLink_EmptyList() {
		// Arrange
		List<String> links = List.of();

		// Act
		String firstLink = orderItemMapper.mapFirstLink(links);

		// Assert using AssertJ
		assertThat(firstLink).isNull();

		// Assert using Hamcrest
		assertThat(firstLink, is(nullValue()));
	}

	@Test
	void testMapFirstLink_NullList() {
		// Arrange
		List<String> links = null;

		// Act
		String firstLink = orderItemMapper.mapFirstLink(links);

		// Assert using AssertJ
		assertThat(firstLink).isNull();

		// Assert using Hamcrest
		assertThat(firstLink, is(nullValue()));
	}
}