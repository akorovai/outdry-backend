package dev.akorovai.backend.user_role;

import com.fasterxml.jackson.annotation.JsonIgnore;
import dev.akorovai.backend.role.Role;
import dev.akorovai.backend.user.User;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "user_role", indexes = {
		@Index(name = "idx_user_role_user_id_role_id", columnList = "user_id, role_id")
})
@EntityListeners(AuditingEntityListener.class)
public class UserRole {
	@EmbeddedId
	private UserRoleId id;

	@ManyToOne(fetch = FetchType.LAZY)
	@MapsId("userId")
	@JoinColumn(name = "user_id")
	@ToString.Exclude
	@JsonIgnore
	private User user;

	@ManyToOne(fetch = FetchType.LAZY)
	@MapsId("roleId")
	@JoinColumn(name = "role_id")
	@ToString.Exclude
	@JsonIgnore
	private Role role;

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		UserRole userRole = (UserRole) o;
		return id != null && id.equals(userRole.id);
	}

	@Override
	public int hashCode() {
		return getClass().hashCode();
	}
}