package dev.akorovai.backend.security;

import com.github.javafaker.Faker;
import dev.akorovai.backend.user.User;
import dev.akorovai.backend.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserDetailsServiceImplTest {

	@Mock
	private UserRepository userRepository;

	@InjectMocks
	private UserDetailsServiceImpl userDetailsService;

	private Faker faker;

	@BeforeEach
	void setUp() {
		faker = new Faker();
	}

	@Test
	void loadUserByUsername_WhenUserExists_ReturnsUserDetails() {
		// Arrange
		String nicknameOrEmail = faker.internet().emailAddress();
		User user = User.builder()
				            .id(faker.number().randomNumber())
				            .nickname(faker.name().username())
				            .email(nicknameOrEmail)
				            .password(faker.internet().password())
				            .build();

		when(userRepository.loadByNicknameOrEmail(nicknameOrEmail)).thenReturn(Optional.of(user));

		// Act
		UserDetails userDetails = userDetailsService.loadUserByUsername(nicknameOrEmail);

		// Assert
		assertThat(userDetails).isNotNull();
		assertThat(userDetails.getUsername()).isEqualTo(nicknameOrEmail);
		assertThat(userDetails.getPassword()).isEqualTo(user.getPassword());
	}

	@Test
	void loadUserByUsername_WhenUserDoesNotExist_ThrowsUsernameNotFoundException() {
		// Arrange
		String nicknameOrEmail = faker.internet().emailAddress();

		when(userRepository.loadByNicknameOrEmail(nicknameOrEmail)).thenReturn(Optional.empty());

		// Act & Assert
		assertThatThrownBy(() -> userDetailsService.loadUserByUsername(nicknameOrEmail))
				.isInstanceOf(UsernameNotFoundException.class)
				.hasMessage("User not found");
	}
}