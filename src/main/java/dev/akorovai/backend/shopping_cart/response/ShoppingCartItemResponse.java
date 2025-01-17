package dev.akorovai.backend.shopping_cart.response;

import dev.akorovai.backend.product.response.ProductCartResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class ShoppingCartItemResponse {


	private Long id;

	private Integer quantity;

	private ProductCartResponse product;

	private Long userId;
}
