package dev.akorovai.backend.shopping_cart;

import com.github.javafaker.Faker;
import dev.akorovai.backend.security.ResponseRecord;
import dev.akorovai.backend.shopping_cart.response.ShoppingCartItemResponse;
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

import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class ShoppingCartControllerTest {

	@Mock
	private ShoppingCartItemService shoppingCartItemService;

	@InjectMocks
	private ShoppingCartController shoppingCartController;

	private MockMvc mockMvc;
	private Faker faker;

	@BeforeEach
	void setUp() {
		mockMvc = MockMvcBuilders.standaloneSetup(shoppingCartController).build();
		faker = new Faker();
	}

	@Test
	void getShoppingCartItems_ReturnsListOfItems() throws Exception {
		// Arrange
		ShoppingCartItemResponse itemResponse = ShoppingCartItemResponse.builder()
				                                        .id(faker.number().randomNumber())
				                                        .quantity(faker.number().numberBetween(1, 10))
				                                        .userId(faker.number().randomNumber())
				                                        .build();
		List<ShoppingCartItemResponse> cartItems = Collections.singletonList(itemResponse);

		when(shoppingCartItemService.getShoppingCartItemsByUserId()).thenReturn(cartItems);

		// Act & Assert
		mockMvc.perform(get("/api/shopping-cart/items")
				                .contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.code", is(HttpStatus.OK.value())))
				.andExpect(jsonPath("$.message", hasSize(1)))
				.andExpect(jsonPath("$.message[0].id", is(itemResponse.getId().intValue())))
				.andExpect(jsonPath("$.message[0].quantity", is(itemResponse.getQuantity())))
				.andExpect(jsonPath("$.message[0].userId", is(itemResponse.getUserId().intValue())));

		verify(shoppingCartItemService).getShoppingCartItemsByUserId();
	}

	@Test
	void deleteShoppingCartItem_DeletesItemSuccessfully() throws Exception {
		// Arrange
		Long itemId = faker.number().randomNumber();

		// Act & Assert
		mockMvc.perform(delete("/api/shopping-cart/items/{itemId}", itemId)
				                .contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNoContent())
				.andExpect(jsonPath("$.code", is(HttpStatus.NO_CONTENT.value())))
				.andExpect(jsonPath("$.message", is("Shopping cart item deleted successfully")));

		verify(shoppingCartItemService).deleteShoppingCartItem(itemId);
	}

	@Test
	void updateShoppingCartItemQuantity_UpdatesQuantitySuccessfully() throws Exception {
		// Arrange
		Long itemId = faker.number().randomNumber();
		Integer newQuantity = faker.number().numberBetween(1, 10);

		// Act & Assert
		mockMvc.perform(patch("/api/shopping-cart/items/{itemId}/quantity", itemId)
				                .param("newQuantity", newQuantity.toString())
				                .contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNoContent())
				.andExpect(jsonPath("$.code", is(HttpStatus.NO_CONTENT.value())))
				.andExpect(jsonPath("$.message", is("Shopping cart item quantity updated successfully")));

		verify(shoppingCartItemService).updateShoppingCartItemQuantity(itemId, newQuantity);
	}

	@Test
	void addProductToCart_AddsProductSuccessfully() throws Exception {
		// Arrange
		Long productId = faker.number().randomNumber();

		// Act & Assert
		mockMvc.perform(post("/api/shopping-cart/items")
				                .param("productId", productId.toString())
				                .contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNoContent())
				.andExpect(jsonPath("$.code", is(HttpStatus.NO_CONTENT.value())))
				.andExpect(jsonPath("$.message", is("Product added to cart successfully")));

		verify(shoppingCartItemService).addProductToCart(productId);
	}
}