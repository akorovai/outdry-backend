package dev.akorovai.backend.refresh_token;

import dev.akorovai.backend.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "refresh_tokens")
public class RefreshToken {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, unique = true, columnDefinition = "TEXT")
	private String token;

	@OneToOne
	@JoinColumn(name = "user_id", referencedColumnName = "id")
	private User user;

	@Column(nullable = false, name = "expires_at")
	private Instant expiresAt;

}
