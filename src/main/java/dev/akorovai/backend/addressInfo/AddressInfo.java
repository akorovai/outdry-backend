package dev.akorovai.backend.addressInfo;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
@Table(name = "address_info", indexes = {
		@Index(name = "idx_address_info_user_id", columnList = "user_id"),
		@Index(name = "idx_address_info_state_city_postal_code", columnList = "state, city, postal_code")
})
@EntityListeners(AuditingEntityListener.class)

public class AddressInfo {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, length = 128)
	private String fullName;

	@Column(nullable = false, length = 64)
	private String state;

	@Column(nullable = false, length = 64)
	private String street;

	@Column(nullable = false, length = 64)
	private String apartment;

	@Column(nullable = false, length = 64)
	private String postalCode;

	@Column(nullable = false, length = 64)
	private String city;

	@Column(nullable = false, length = 64)
	private String phone;

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
		AddressInfo that = (AddressInfo) o;
		return id != null && id.equals(that.id);
	}

	@Override
	public int hashCode() {
		return getClass().hashCode();
	}
}