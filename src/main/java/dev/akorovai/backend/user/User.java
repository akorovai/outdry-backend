package dev.akorovai.backend.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import dev.akorovai.backend.address_info.AddressInfo;
import dev.akorovai.backend.emailToken.EmailToken;
import dev.akorovai.backend.order.Order;
import dev.akorovai.backend.review.Review;
import dev.akorovai.backend.role.Role;
import dev.akorovai.backend.shopping_cart.ShoppingCartItem;
import dev.akorovai.backend.wish_list.WishListItem;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Table(name = "users", indexes = {
		@Index(name = "idx_user_email", columnList = "email", unique = true),
		@Index(name = "idx_user_refresh_token", columnList = "refresh_token")
})
@EntityListeners(AuditingEntityListener.class)
public class User implements UserDetails, Principal {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(nullable = false)
	private Long id;

	@Column(nullable = false, length = 128)
	private String nickname;

	@Column(nullable = false, unique = true, length = 64)
	private String email;

	@Column(nullable = false)
	private String password;

	@Column(name = "refresh_token")
	private String refreshToken;

	@Builder.Default
	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "user_roles",
			joinColumns = @JoinColumn(name = "user_id"),
			inverseJoinColumns = @JoinColumn(name = "role_id"))
	private Set<Role> roles = new HashSet<>();

	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@ToString.Exclude
	@JsonIgnore
	@Builder.Default
	private Set<AddressInfo> addresses = new HashSet<>();

	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@ToString.Exclude
	@JsonIgnore
	@Builder.Default
	private Set<ShoppingCartItem> shoppingCartItems = new HashSet<>();

	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@ToString.Exclude
	@JsonIgnore
	@Builder.Default
	private Set<WishListItem> wishListItems = new HashSet<>();

	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@ToString.Exclude
	@JsonIgnore
	@Builder.Default
	private Set<Review> reviews = new HashSet<>();

	@Builder.Default
	@OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<EmailToken> emailTokens = new HashSet<>();

	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@ToString.Exclude
	@JsonIgnore
	@Builder.Default
	private Set<Order> orders = new HashSet<>();

	@CreatedDate
	@Column(nullable = false, updatable = false)
	private LocalDateTime createdDate;

	@Column(name = "account_locked", nullable = false)
	private boolean accountLocked;

	@Column(nullable = false)
	private boolean enabled;



	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return this.roles.stream()
				       .map(role -> new SimpleGrantedAuthority(role.getName()))
				       .collect(Collectors.toList());
	}

	@Override
	public String getUsername() {
		return email;
	}

	@Override
	public boolean isAccountNonLocked() {
		return !accountLocked;
	}

	@Override
	public boolean isEnabled() {
		return enabled;
	}

	@Override
	public String getName() {
		return email;
	}
}