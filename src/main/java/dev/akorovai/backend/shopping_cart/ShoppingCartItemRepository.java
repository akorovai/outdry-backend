package dev.akorovai.backend.shopping_cart;

import dev.akorovai.backend.product.Product;
import dev.akorovai.backend.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ShoppingCartItemRepository extends JpaRepository<ShoppingCartItem, Long> {

	@Query("SELECT sci FROM ShoppingCartItem sci JOIN FETCH sci.product WHERE sci.user.id = :userId")
	List<ShoppingCartItem> findByUserIdWithProduct(@Param("userId") Long userId);

	@Query("SELECT sci FROM ShoppingCartItem sci JOIN FETCH sci.product WHERE sci.id = :itemId")
	Optional<ShoppingCartItem> findByIdWithProduct(@Param("itemId") Long itemId);

	Optional<ShoppingCartItem> findByUserAndProduct( User user, Product product);
}