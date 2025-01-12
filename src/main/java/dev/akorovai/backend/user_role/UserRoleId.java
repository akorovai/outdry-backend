package dev.akorovai.backend.user_role;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Embeddable
public class UserRoleId implements Serializable {
	private Long userId;
	private Long roleId;

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		UserRoleId that = (UserRoleId) o;
		return Objects.equals(userId, that.userId) && Objects.equals(roleId, that.roleId);
	}

	@Override
	public int hashCode() {
		return Objects.hash(userId, roleId);
	}
}