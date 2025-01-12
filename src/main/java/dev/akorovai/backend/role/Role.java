package dev.akorovai.backend.role;

import com.fasterxml.jackson.annotation.JsonIgnore;
import dev.akorovai.backend.user_role.UserRole;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "role", indexes = {
		@Index(name = "idx_role_name", columnList = "name")
})
@EntityListeners(AuditingEntityListener.class)
public class Role {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, length = 8)
	private String name;

	@OneToMany(mappedBy = "role", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@ToString.Exclude
	@JsonIgnore
	private Set<UserRole> userRoles;

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Role role = (Role) o;
		return id != null && id.equals(role.id);
	}

	@Override
	public int hashCode() {
		return getClass().hashCode();
	}
}