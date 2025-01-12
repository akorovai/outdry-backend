package dev.akorovai.backend.shopping_cart;

import com.fasterxml.jackson.annotation.JsonIgnore;
import dev.akorovai.backend.product.Product;
import dev.akorovai.backend.user.User;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "shopping_cart_item", indexes = {
		@Index(name = "idx_shopping_cart_item_user_id", columnList = "user_id"),
		@Index(name = "idx_shopping_cart_item_product_id", columnList = "product_id")
})
@EntityListeners(AuditingEntityListener.class)
public class ShoppingCartItem {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private Integer quantity;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "product_id", nullable = false)
	@ToString.Exclude
	@JsonIgnore
	private Product product;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	@ToString.Exclude
	@JsonIgnore
	private User user;

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

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		ShoppingCartItem that = (ShoppingCartItem) o;
		return id != null && id.equals(that.id);
	}

	@Override
	public int hashCode() {
		return getClass().hashCode();
	}
}