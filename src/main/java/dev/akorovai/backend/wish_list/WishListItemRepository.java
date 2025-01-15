package dev.akorovai.backend.wish_list;

import dev.akorovai.backend.product.Product;
import dev.akorovai.backend.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WishListItemRepository extends JpaRepository<WishListItem, Long> {

    List<WishListItem> findWishListItemsByUser(User user);

    @Query("SELECT wl.product FROM WishListItem wl " +
            "WHERE wl.user = :user " +
            "AND (:type IS NULL OR wl.product.type.name = :type) " +
            "AND (:gender IS NULL OR wl.product.gender = :gender) " +
            "AND (:color IS NULL OR wl.product.color.name = :color) " +
            "AND (:size IS NULL OR wl.product.size = :size) " +
            "AND (:minPrice IS NULL OR wl.product.price >= :minPrice) " +
            "AND (:maxPrice IS NULL OR wl.product.price <= :maxPrice)")
    List<Product> findWishListProductsByUserAndFilters(
            @Param("user") User user,
            @Param("type") String type,
            @Param("gender") String gender,
            @Param("color") String color,
            @Param("size") String size,
            @Param("minPrice") Double minPrice,
            @Param("maxPrice") Double maxPrice
    );

    int deleteAllByUser(User user);

    int deleteByProductId(Long productId);

    boolean existsByProductAndUser(Product product, User user);

}