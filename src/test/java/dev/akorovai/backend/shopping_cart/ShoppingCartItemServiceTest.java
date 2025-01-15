package dev.akorovai.backend.shopping_cart;


import com.github.javafaker.Faker;
import dev.akorovai.backend.handler.shopping_cart.*;
import dev.akorovai.backend.product.Product;
import dev.akorovai.backend.product.ProductRepository;
import dev.akorovai.backend.product.mapper.ProductMapper;
import dev.akorovai.backend.product.response.ProductCartResponse;
import dev.akorovai.backend.security.JwtService;
import dev.akorovai.backend.shopping_cart.mapper.ShoppingCartItemMapper;
import dev.akorovai.backend.shopping_cart.response.ShoppingCartItemResponse;
import dev.akorovai.backend.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ShoppingCartItemServiceTest {

	@Mock
	private ShoppingCartItemRepository shoppingCartItemRepository;

	@Mock
	private ShoppingCartItemMapper shoppingCartItemMapper;

	@Mock
	private JwtService jwtService;

	@Mock
	private ProductRepository productRepository;

	@InjectMocks
	private ShoppingCartItemService shoppingCartItemService;

	@Mock
	private ProductMapper productMapper;


	private Faker faker;
	private User user;
	private Product product;
	private ShoppingCartItem shoppingCartItem;

	@BeforeEach
	void setUp() {
		faker = new Faker();
		user = User.builder()
				       .id(faker.number().randomNumber())
				       .build();
		product = Product.builder()
				          .id(faker.number().randomNumber())
				          .amount(faker.number().numberBetween(1, 100))
				          .build();
		shoppingCartItem = ShoppingCartItem.builder()
				                   .id(faker.number().randomNumber())
				                   .user(user)
				                   .product(product)
				                   .quantity(faker.number().numberBetween(1, 10))
				                   .build();
	}

	@Nested
	class GetShoppingCartItemsByUserIdTests {

		@Test
		void whenUserHasItemsInCart_thenReturnInStockItems() {
			// Arrange
			when(jwtService.getAuthenticatedUser()).thenReturn(user);
			when(shoppingCartItemRepository.findByUserIdWithProduct(user.getId()))
					.thenReturn(List.of(shoppingCartItem));

			// Create a valid ProductCartResponse
			ProductCartResponse productCartResponse = ProductCartResponse.builder()
					                                          .id(product.getId())
					                                          .name(faker.commerce().productName()) // Use DataFaker for product name
					                                          .color(faker.color().name()) // Use DataFaker for color
					                                          .price(faker.number().randomDouble(2, 10, 100)) // Use DataFaker for price
					                                          .discount(faker.number().numberBetween(0, 50)) // Use DataFaker for discount
					                                          .amount(faker.number().numberBetween(1, 10)) // Use DataFaker for amount
					                                          .imageUrl(faker.internet().image()) // Use DataFaker for image URL
					                                          .warehouseAmount(faker.number().numberBetween(10, 100)) // Use DataFaker for warehouse amount
					                                          .build();

			// Mock the mapper to return a valid ShoppingCartItemResponse
			when(shoppingCartItemMapper.toShoppingCartItemResponse(shoppingCartItem))
					.thenReturn(ShoppingCartItemResponse.builder()
							            .id(shoppingCartItem.getId())
							            .quantity(shoppingCartItem.getQuantity())
							            .product(productCartResponse) // Provide the valid ProductCartResponse
							            .userId(shoppingCartItem.getUser().getId())
							            .build());

			// Act
			List<ShoppingCartItemResponse> result = shoppingCartItemService.getShoppingCartItemsByUserId();

			// Assert
			assertThat(result).hasSize(1);
			verify(shoppingCartItemRepository).findByUserIdWithProduct(user.getId());
			verify(shoppingCartItemMapper).toShoppingCartItemResponse(shoppingCartItem);
		}

		@Test
		void whenUserHasOutOfStockItems_thenRemoveOutOfStockItems() {
			// Arrange
			Product outOfStockProduct = Product.builder()
					                            .id(faker.number().randomNumber())
					                            .amount(0) // Out of stock
					                            .build();
			ShoppingCartItem outOfStockItem = ShoppingCartItem.builder()
					                                  .id(faker.number().randomNumber())
					                                  .user(user)
					                                  .product(outOfStockProduct)
					                                  .quantity(faker.number().numberBetween(1, 10))
					                                  .build();

			when(jwtService.getAuthenticatedUser()).thenReturn(user);
			when(shoppingCartItemRepository.findByUserIdWithProduct(user.getId()))
					.thenReturn(List.of(shoppingCartItem, outOfStockItem));

			// Act
			shoppingCartItemService.getShoppingCartItemsByUserId();

			// Assert
			verify(shoppingCartItemRepository).deleteAll(List.of(outOfStockItem));
		}
	}

	@Nested
	class DeleteShoppingCartItemTests {

		@Test
		void whenItemExistsAndUserIsAuthorized_thenDeleteItem() {
			// Arrange
			when(jwtService.getAuthenticatedUser()).thenReturn(user);
			when(shoppingCartItemRepository.findById(shoppingCartItem.getId()))
					.thenReturn(Optional.of(shoppingCartItem));

			// Act
			shoppingCartItemService.deleteShoppingCartItem(shoppingCartItem.getId());

			// Assert
			verify(shoppingCartItemRepository).delete(shoppingCartItem);
		}

		@Test
		void whenItemDoesNotExist_thenThrowShoppingCartItemNotFoundException() {
			// Arrange
			when(jwtService.getAuthenticatedUser()).thenReturn(user);
			when(shoppingCartItemRepository.findById(shoppingCartItem.getId()))
					.thenReturn(Optional.empty());

			// Act & Assert
			assertThrows(ShoppingCartItemNotFoundException.class,
					() -> shoppingCartItemService.deleteShoppingCartItem(shoppingCartItem.getId()));
		}

		@Test
		void whenUserIsNotAuthorized_thenThrowUnauthorizedItemDeletionException() {
			// Arrange
			User unauthorizedUser = User.builder()
					                        .id(faker.number().randomNumber())
					                        .build();
			when(jwtService.getAuthenticatedUser()).thenReturn(unauthorizedUser);
			when(shoppingCartItemRepository.findById(shoppingCartItem.getId()))
					.thenReturn(Optional.of(shoppingCartItem));

			// Act & Assert
			assertThrows(UnauthorizedItemDeletionException.class,
					() -> shoppingCartItemService.deleteShoppingCartItem(shoppingCartItem.getId()));
		}
	}

	@Nested
	class UpdateShoppingCartItemQuantityTests {

		@Test
		void whenItemExistsAndUserIsAuthorized_thenUpdateQuantity() {
			// Arrange
			int newQuantity = faker.number().numberBetween(1, product.getAmount());
			when(jwtService.getAuthenticatedUser()).thenReturn(user);
			when(shoppingCartItemRepository.findByIdWithProduct(shoppingCartItem.getId()))
					.thenReturn(Optional.of(shoppingCartItem));

			// Act
			shoppingCartItemService.updateShoppingCartItemQuantity(shoppingCartItem.getId(), newQuantity);

			// Assert
			assertThat(shoppingCartItem.getQuantity()).isEqualTo(newQuantity);
			verify(shoppingCartItemRepository).save(shoppingCartItem);
		}

		@Test
		void whenItemDoesNotExist_thenThrowShoppingCartItemNotFoundException() {
			// Arrange
			when(jwtService.getAuthenticatedUser()).thenReturn(user);
			when(shoppingCartItemRepository.findByIdWithProduct(shoppingCartItem.getId()))
					.thenReturn(Optional.empty());

			// Act & Assert
			assertThrows(ShoppingCartItemNotFoundException.class,
					() -> shoppingCartItemService.updateShoppingCartItemQuantity(shoppingCartItem.getId(), 3));
		}

		@Test
		void whenUserIsNotAuthorized_thenThrowUnauthorizedItemModificationException() {
			// Arrange
			User unauthorizedUser = User.builder()
					                        .id(faker.number().randomNumber())
					                        .build();
			when(jwtService.getAuthenticatedUser()).thenReturn(unauthorizedUser);
			when(shoppingCartItemRepository.findByIdWithProduct(shoppingCartItem.getId()))
					.thenReturn(Optional.of(shoppingCartItem));

			// Act & Assert
			assertThrows(UnauthorizedItemModificationException.class,
					() -> shoppingCartItemService.updateShoppingCartItemQuantity(shoppingCartItem.getId(), 3));
		}

		@Test
		void whenQuantityIsInvalid_thenThrowIllegalArgumentException() {
			// Arrange
			when(jwtService.getAuthenticatedUser()).thenReturn(user);
			when(shoppingCartItemRepository.findByIdWithProduct(shoppingCartItem.getId()))
					.thenReturn(Optional.of(shoppingCartItem));

			// Act & Assert
			assertThrows(IllegalArgumentException.class,
					() -> shoppingCartItemService.updateShoppingCartItemQuantity(shoppingCartItem.getId(), 0));
		}

		@Test
		void whenQuantityExceedsStock_thenThrowInsufficientStockException() {
			// Arrange
			int invalidQuantity = product.getAmount() + 1;
			when(jwtService.getAuthenticatedUser()).thenReturn(user);
			when(shoppingCartItemRepository.findByIdWithProduct(shoppingCartItem.getId()))
					.thenReturn(Optional.of(shoppingCartItem));

			// Act & Assert
			assertThrows(InsufficientStockException.class,
					() -> shoppingCartItemService.updateShoppingCartItemQuantity(shoppingCartItem.getId(), invalidQuantity));
		}
	}

	@Nested
	class AddProductToCartTests {

		@Test
		void whenProductExistsAndItemExists_thenUpdateQuantity() {
			// Arrange
			when(jwtService.getAuthenticatedUser()).thenReturn(user);
			when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));
			when(shoppingCartItemRepository.findByUserAndProduct(user, product))
					.thenReturn(Optional.of(shoppingCartItem));

			// Capture the original quantity
			int originalQuantity = shoppingCartItem.getQuantity();

			// Act
			shoppingCartItemService.addProductToCart(product.getId());

			// Assert
			assertThat(shoppingCartItem.getQuantity()).isEqualTo(originalQuantity + 1);
			verify(shoppingCartItemRepository).save(shoppingCartItem);
		}

		@Test
		void whenProductExistsAndItemDoesNotExist_thenAddNewItem() {
			// Arrange
			when(jwtService.getAuthenticatedUser()).thenReturn(user);
			when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));
			when(shoppingCartItemRepository.findByUserAndProduct(user, product)).thenReturn(Optional.empty());

			// Act
			shoppingCartItemService.addProductToCart(product.getId());

			// Assert
			verify(shoppingCartItemRepository).save(Mockito.any(ShoppingCartItem.class));
		}

		@Test
		void whenProductDoesNotExist_thenThrowIllegalArgumentException() {
			// Arrange
			when(jwtService.getAuthenticatedUser()).thenReturn(user);
			when(productRepository.findById(product.getId())).thenReturn(Optional.empty());

			// Act & Assert
			assertThrows(IllegalArgumentException.class,
					() -> shoppingCartItemService.addProductToCart(product.getId()));
		}

		@Test
		void whenQuantityExceedsStock_thenThrowInsufficientStockException() {
			// Arrange
			product.setAmount(1); // Only 1 item in stock
			when(jwtService.getAuthenticatedUser()).thenReturn(user);
			when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));
			when(shoppingCartItemRepository.findByUserAndProduct(user, product))
					.thenReturn(Optional.of(shoppingCartItem));

			// Act & Assert
			assertThrows(InsufficientStockException.class,
					() -> shoppingCartItemService.addProductToCart(product.getId()));
		}
	}
}