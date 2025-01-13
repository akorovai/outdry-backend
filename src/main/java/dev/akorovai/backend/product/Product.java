package dev.akorovai.backend.product;

import com.fasterxml.jackson.annotation.JsonIgnore;
import dev.akorovai.backend.color.Color;
import dev.akorovai.backend.orderItem.OrderItem;
import dev.akorovai.backend.review.Review;
import dev.akorovai.backend.shopping_cart.ShoppingCartItem;
import dev.akorovai.backend.type.Type;
import dev.akorovai.backend.wish_list.WishListItem;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "product", indexes = {
		@Index(name = "idx_product_color_id", columnList = "color_id"),
		@Index(name = "idx_product_type_id", columnList = "type_id"),
		@Index(name = "idx_product_name", columnList = "name")
})
public class Product {
	@Id
	@Column(nullable = false, length = 16)
	private String id;

	@Column(nullable = false, length = 128)
	private String name;

	@Column(nullable = false, precision = 6)
	private Double price;

	@Column(nullable = false)
	private Boolean inStock;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "color_id", nullable = false)
	private Color color;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "type_id", nullable = false)
	private Type type;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private Gender gender;

	@Column(nullable = false)
	private Integer amount;

	@Column
	private Integer discount;

	@OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@ToString.Exclude
	@JsonIgnore
	private Set<OrderItem> orderItems;

	@OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@ToString.Exclude
	@JsonIgnore
	private Set<ShoppingCartItem> shoppingCartItems;

	@OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@ToString.Exclude
	@JsonIgnore
	private Set<WishListItem> wishListItems;

	@OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@ToString.Exclude
	@JsonIgnore
	private Set<Review> reviews;

	@CreatedBy
	@Column(nullable = false, updatable = false)
	private String createdBy;

	@CreatedDate
	@Column(nullable = false, updatable = false)
	private LocalDateTime createdDate;

	@LastModifiedBy
	@Column(nullable = false)
	private String lastModifiedBy;

	@LastModifiedDate
	@Column(nullable = false)
	private LocalDateTime lastModifiedDate;

}