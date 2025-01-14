package dev.akorovai.backend.shopping_cart;

import dev.akorovai.backend.security.ResponseRecord;
import dev.akorovai.backend.shopping_cart.response.ShoppingCartItemResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/shopping-cart")
@RequiredArgsConstructor
public class ShoppingCartController {

    private final ShoppingCartItemService shoppingCartItemService;

    @GetMapping("/items")
    public ResponseRecord getShoppingCartItems() {
        List<ShoppingCartItemResponse> cartItems = shoppingCartItemService.getShoppingCartItemsByUserId();
        return ResponseRecord.builder()
                       .code(HttpStatus.OK.value())
                       .message(cartItems)
                       .build();
    }

    @DeleteMapping("/items/{itemId}")
    public ResponseRecord deleteShoppingCartItem(@PathVariable Long itemId) {
        shoppingCartItemService.deleteShoppingCartItem(itemId);
        return ResponseRecord.builder()
                       .code(HttpStatus.NO_CONTENT.value())
                       .message("Shopping cart item deleted successfully")
                       .build();
    }

    @PatchMapping("/items/{itemId}/quantity")
    public ResponseRecord updateShoppingCartItemQuantity(
            @PathVariable Long itemId,
            @RequestParam Integer newQuantity
    ) {
        shoppingCartItemService.updateShoppingCartItemQuantity(itemId, newQuantity);
        return ResponseRecord.builder()
                       .code(HttpStatus.NO_CONTENT.value())
                       .message("Shopping cart item quantity updated successfully")
                       .build();
    }

    @PostMapping("/items")
    public ResponseRecord addProductToCart(@RequestParam Long productId) {
        shoppingCartItemService.addProductToCart(productId);
        return ResponseRecord.builder()
                       .code(HttpStatus.NO_CONTENT.value())
                       .message("Product added to cart successfully")
                       .build();
    }
}