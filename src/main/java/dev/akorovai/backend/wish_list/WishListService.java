package dev.akorovai.backend.wish_list;

import dev.akorovai.backend.handler.product.ProductNotFoundException;
import dev.akorovai.backend.product.ProductRepository;
import dev.akorovai.backend.product.mapper.ProductMapper;
import dev.akorovai.backend.product.response.ProductResponse;
import dev.akorovai.backend.user.User;
import dev.akorovai.backend.user.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import dev.akorovai.backend.product.Product;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WishListService {

    private final WishListItemRepository wishListItemRepository;
    private final UserService userService;
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    @Transactional
    public List<ProductResponse> getWishListProducts(
            String type,
            String gender,
            String color,
            String size,
            Double minPrice,
            Double maxPrice
    ) {
        User user = userService.getAuthenticatedUser();

        List<Product> products = wishListItemRepository.findWishListProductsByUserAndFilters(
                user, type, gender, color, size, minPrice, maxPrice
        );

        return products.stream()
                .map(productMapper::toProductResponse)
                .toList();
    }

    @Transactional
    public boolean deleteWishListItem(Long productId) {
        return wishListItemRepository.deleteByProductId(productId) > 0;
    }

    @Transactional
    public boolean deleteAllWishListItems() {
        User user = userService.getAuthenticatedUser();
        return wishListItemRepository.deleteAllByUser(user) > 0;
    }

    @Transactional
    public boolean addToWishList(Long productId) {
        User user = userService.getAuthenticatedUser();
        Product product = productRepository.findById(productId).
                orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + productId));
        boolean exists = wishListItemRepository.existsByProductAndUser(product, user);
        if (exists) {
            return false;
        }

        WishListItem wishListItem = WishListItem.builder()
                .product(product)
                .user(user)
                .build();

        wishListItemRepository.save(wishListItem);
        return true;
    }

}