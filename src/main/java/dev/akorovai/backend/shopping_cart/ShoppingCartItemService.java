package dev.akorovai.backend.shopping_cart;

import dev.akorovai.backend.handler.shopping_cart.ShoppingCartItemNotFoundException;
import dev.akorovai.backend.handler.shopping_cart.UnauthorizedItemDeletionException;
import dev.akorovai.backend.handler.shopping_cart.UnauthorizedItemModificationException;
import dev.akorovai.backend.handler.shopping_cart.InsufficientStockException;
import dev.akorovai.backend.security.JwtService;
import dev.akorovai.backend.shopping_cart.mapper.ShoppingCartItemMapper;
import dev.akorovai.backend.shopping_cart.response.ShoppingCartItemResponse;
import dev.akorovai.backend.user.User;
import dev.akorovai.backend.product.Product;
import dev.akorovai.backend.product.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ShoppingCartItemService {

	private final ShoppingCartItemRepository shoppingCartItemRepository;
	private final ShoppingCartItemMapper shoppingCartItemMapper;
	private final JwtService jwtService;
	private final ProductRepository productRepository;

	@Transactional(readOnly = true)
	public List<ShoppingCartItemResponse> getShoppingCartItemsByUserId() {
		User user = jwtService.getAuthenticatedUser();

		List<ShoppingCartItem> shoppingCartItems = shoppingCartItemRepository.findByUserIdWithProduct(user.getId());

		List<ShoppingCartItem> outOfStockItems = shoppingCartItems.stream()
				                                         .filter(item -> item.getProduct().getAmount() <= 0)
				                                         .collect(Collectors.toList());

		if (!outOfStockItems.isEmpty()) {
			shoppingCartItemRepository.deleteAll(outOfStockItems);
		}

		return shoppingCartItems.stream()
				       .filter(item -> item.getProduct().getAmount() > 0)
				       .map(shoppingCartItemMapper::toShoppingCartItemResponse)
				       .collect(Collectors.toList());
	}

	@Transactional
	public void deleteShoppingCartItem(Long itemId) {
		User user = jwtService.getAuthenticatedUser();
		ShoppingCartItem item = shoppingCartItemRepository.findById(itemId)
				                        .orElseThrow(() -> new ShoppingCartItemNotFoundException(
						                        "Shopping cart item with ID " + itemId + " not found"
				                        ));

		if (!item.getUser().getId().equals(user.getId())) {
			throw new UnauthorizedItemDeletionException(
					"User with ID " + user.getId() + " is not authorized to delete shopping cart item with ID " + itemId
			);
		}

		shoppingCartItemRepository.delete(item);
	}

	@Transactional
	public void updateShoppingCartItemQuantity(Long itemId, Integer newQuantity) {
		User user = jwtService.getAuthenticatedUser();
		ShoppingCartItem item = shoppingCartItemRepository.findByIdWithProduct(itemId)
				                        .orElseThrow(() -> new ShoppingCartItemNotFoundException(
						                        "Shopping cart item with ID " + itemId + " not found"
				                        ));

		if (!item.getUser().getId().equals(user.getId())) {
			throw new UnauthorizedItemModificationException(
					"User with ID " + user.getId() + " is not authorized to modify shopping cart item with ID " + itemId
			);
		}

		validateQuantity(newQuantity, item.getProduct().getAmount());

		item.setQuantity(newQuantity);
		shoppingCartItemRepository.save(item);
	}

	@Transactional
	public void addProductToCart(Long productId) {
		User user = jwtService.getAuthenticatedUser();
		Product product = productRepository.findById(productId)
				                  .orElseThrow(() -> new IllegalArgumentException("Product with ID " + productId + " not found"));

		Optional<ShoppingCartItem> existingItem = shoppingCartItemRepository.findByUserAndProduct(user, product);

		if (existingItem.isPresent()) {
			ShoppingCartItem item = existingItem.get();
			int newQuantity = item.getQuantity() + 1;

			validateQuantity(newQuantity, product.getAmount());

			item.setQuantity(newQuantity);
			shoppingCartItemRepository.save(item);
		} else {
			validateQuantity(1, product.getAmount());

			ShoppingCartItem newItem = ShoppingCartItem.builder()
					                           .user(user)
					                           .product(product)
					                           .quantity(1)
					                           .build();

			shoppingCartItemRepository.save(newItem);
		}
	}

	private void validateQuantity(int quantity, int availableStock) {
		if (quantity <= 0) {
			throw new IllegalArgumentException("Quantity must be greater than 0");
		}
		if (quantity > availableStock) {
			throw new InsufficientStockException(
					"Insufficient stock. Available stock: " + availableStock
			);
		}
	}
}