package dev.akorovai.backend.color;

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
@Table(name = "color", indexes = {
		@Index(name = "idx_color_name", columnList = "name"),
		@Index(name = "idx_color_code", columnList = "code")
})
public class Color {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, length = 16)
	private String name;

	@Column
	private Integer code;

	@OneToMany(mappedBy = "color", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@ToString.Exclude
	@JsonIgnore
	private Set<Product> products;

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Color color = (Color) o;
		return id != null && id.equals(color.id);
	}

	@Override
	public int hashCode() {
		return getClass().hashCode();
	}
}