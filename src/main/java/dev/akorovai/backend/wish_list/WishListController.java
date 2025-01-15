package dev.akorovai.backend.wish_list;

import dev.akorovai.backend.product.response.ProductResponse;
import dev.akorovai.backend.security.ResponseRecord;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/wishlist")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class WishListController {

    private final WishListService wishListService;

    @GetMapping("/products")
    public ResponseEntity<ResponseRecord> getWishListProducts(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String gender,
            @RequestParam(required = false) String color,
            @RequestParam(required = false) String size,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice
    ) {
        List<ProductResponse> products = wishListService.getWishListProducts(
                type, gender, color, size, minPrice, maxPrice
        );

        return ResponseEntity.ok(new ResponseRecord(HttpStatus.OK.value(), products));
    }

    @DeleteMapping("/delete/{productId}")
    public ResponseEntity<ResponseRecord> deleteWishListItem(@PathVariable Long productId) {
        boolean success = wishListService.deleteWishListItem(productId);
        return success
                ? ResponseEntity.ok(new ResponseRecord(HttpStatus.OK.value(), "Product deleted successfully"))
                : ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ResponseRecord(HttpStatus.NOT_FOUND.value(), "Product not found in wishlist"));
    }

    @DeleteMapping("/delete-all")
    public ResponseEntity<ResponseRecord> deleteAllWishListItems() {
        boolean deletedCount = wishListService.deleteAllWishListItems();
        return deletedCount
                ? ResponseEntity.ok(new ResponseRecord(HttpStatus.OK.value(), "All products deleted successfully"))
                : ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ResponseRecord(HttpStatus.NOT_FOUND.value(), "No products found in wishlist"));
    }

    @PostMapping("/add/{productId}")
    public ResponseEntity<ResponseRecord> addToWishList(@PathVariable Long productId) {
        boolean success = wishListService.addToWishList(productId);
        String message = success ? "Product added to wishlist successfully" : "Failed to add product to wishlist";
        ResponseRecord response = new ResponseRecord(HttpStatus.OK.value(), message);
        return ResponseEntity.ok(response);
    }
}