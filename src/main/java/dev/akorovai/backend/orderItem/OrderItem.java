package dev.akorovai.backend.orderItem;

import com.fasterxml.jackson.annotation.JsonIgnore;
import dev.akorovai.backend.order.Order;
import dev.akorovai.backend.product.Product;
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
@Table(name = "order_item", indexes = {
		@Index(name = "idx_order_item_order_id", columnList = "order_id"),
		@Index(name = "idx_order_item_product_id", columnList = "product_id")
})
@EntityListeners(AuditingEntityListener.class)
public class OrderItem {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private Integer quantity;

	@Column(nullable = false, precision = 6)
	private Double price;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "order_id", nullable = false)
	@ToString.Exclude
	@JsonIgnore
	private Order order;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "product_id", nullable = false)
	@ToString.Exclude
	@JsonIgnore
	private Product product;

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
		OrderItem orderItem = (OrderItem) o;
		return id != null && id.equals(orderItem.id);
	}

	@Override
	public int hashCode() {
		return getClass().hashCode();
	}
}