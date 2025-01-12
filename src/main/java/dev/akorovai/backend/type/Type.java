package dev.akorovai.backend.type;

import com.fasterxml.jackson.annotation.JsonIgnore;
import dev.akorovai.backend.product.Product;
import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "type", indexes = {
		@Index(name = "idx_type_name", columnList = "name")
})
public class Type {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, length = 32)
	private String name;

	@OneToMany(mappedBy = "type", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@ToString.Exclude
	@JsonIgnore
	private Set<Product> products;

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Type type = (Type) o;
		return id != null && id.equals(type.id);
	}

	@Override
	public int hashCode() {
		return getClass().hashCode();
	}
}