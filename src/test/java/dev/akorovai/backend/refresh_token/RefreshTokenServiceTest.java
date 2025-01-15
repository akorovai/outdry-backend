package dev.akorovai.backend.refresh_token;

import com.github.javafaker.Faker;
import dev.akorovai.backend.handler.refresh_token.TokenRefreshException;
import dev.akorovai.backend.security.JwtService;
import dev.akorovai.backend.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;

import java.lang.reflect.Field;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("RefreshTokenService Tests")
class RefreshTokenServiceTest {

	@Mock
	private JwtService jwtService;

	@Mock
	private RefreshTokenRepository refreshTokenRepository;

	@InjectMocks
	private RefreshTokenService refreshTokenService;

	private Faker faker;
	private User user;
	private RefreshToken refreshToken;

	@Value("${application.security.jwt.refresh-token.expiration}")
	private Long refreshTokenDurationMs;

	@BeforeEach
	void setUp() throws NoSuchFieldException, IllegalAccessException {
		faker = new Faker();
		user = User.builder()
				       .id(faker.number().randomNumber())
				       .nickname(faker.name().username())
				       .build();

		refreshToken = RefreshToken.builder()
				               .id(faker.number().randomNumber())
				               .user(user)
				               .token(faker.internet().uuid())
				               .expiresAt(Instant.now().plusMillis(86400000)) // 1 day in the future
				               .build();

		// Manually set the refreshTokenDurationMs field using reflection
		Field refreshTokenDurationMsField = RefreshTokenService.class.getDeclaredField("refreshTokenDurationMs");
		refreshTokenDurationMsField.setAccessible(true);
		refreshTokenDurationMsField.set(refreshTokenService, 86400000L); // 1 day in milliseconds
	}

	@Test
	@DisplayName("Should find refresh token by token string")
	void shouldFindRefreshTokenByToken() {
		// Arrange
		when(refreshTokenRepository.findByToken(refreshToken.getToken()))
				.thenReturn(Optional.of(refreshToken));

		// Act
		Optional<RefreshToken> result = refreshTokenService.findByToken(refreshToken.getToken());

		// Assert
		assertThat(result).isPresent();
		assertThat(result.get()).isEqualTo(refreshToken);

		// Verify Mockito interaction
		verify(refreshTokenRepository, times(1)).findByToken(refreshToken.getToken());
	}

	@Test
	@DisplayName("Should create a new refresh token for a user")
	void shouldCreateRefreshTokenForUser() {
		// Arrange
		String newToken = faker.internet().uuid();
		Map<String, Object> claims = Map.of("username", user.getNickname());


		when(jwtService.generateRefreshToken(claims, user)).thenReturn(newToken);


		when(refreshTokenRepository.save(any(RefreshToken.class))).thenAnswer(invocation -> {
			RefreshToken savedToken = invocation.getArgument(0);
			savedToken.setToken(newToken);
			return savedToken;
		});

		// Act
		RefreshToken result = refreshTokenService.createRefreshToken(user);

		// Assert
		assertThat(result).isNotNull();
		assertThat(result.getToken()).isEqualTo(newToken); // Ensure the token matches
		assertThat(result.getUser()).isEqualTo(user);
		assertThat(result.getExpiresAt()).isAfter(Instant.now());

		// Verify Mockito interaction
		verify(refreshTokenRepository, times(1)).deleteByUser(user);
		verify(jwtService, times(1)).generateRefreshToken(claims, user);
		verify(refreshTokenRepository, times(1)).save(any(RefreshToken.class));
	}
	@Test
	@DisplayName("Should delete refresh token by user")
	void shouldDeleteRefreshTokenByUser() {
		// Arrange
		doNothing().when(refreshTokenRepository).deleteByUser(user);

		// Act
		refreshTokenService.deleteTokenByUser(user);

		// Assert
		verify(refreshTokenRepository, times(1)).deleteByUser(user);
	}

	@Test
	@DisplayName("Should verify expiration of a valid refresh token")
	void shouldVerifyExpirationOfValidRefreshToken() {
		// Act
		RefreshToken result = refreshTokenService.verifyExpiration(refreshToken);

		// Assert
		assertThat(result).isEqualTo(refreshToken);

		// Verify Mockito interaction
		verify(refreshTokenRepository, never()).delete(refreshToken);
	}

	@Test
	@DisplayName("Should throw TokenRefreshException for an expired refresh token")
	void shouldThrowTokenRefreshExceptionForExpiredRefreshToken() {
		// Arrange
		refreshToken.setExpiresAt(Instant.now().minusMillis(1000));

		// Act & Assert
		assertThatThrownBy(() -> refreshTokenService.verifyExpiration(refreshToken))
				.isInstanceOf(TokenRefreshException.class)
				.hasMessage("Refresh token was expired. Please make a new sign-in request");

		// Verify Mockito interaction
		verify(refreshTokenRepository, times(1)).delete(refreshToken);
	}

	@Test
	@DisplayName("Should delete expired tokens")
	void shouldDeleteExpiredTokens() {
		// Arrange
		when(refreshTokenRepository.deleteByExpiresAtBefore(any(Instant.class))).thenReturn(5);

		// Act
		int deletedCount = refreshTokenService.deleteExpiredTokens();

		// Assert
		assertThat(deletedCount).isEqualTo(5);

		// Verify Mockito interaction
		verify(refreshTokenRepository, times(1)).deleteByExpiresAtBefore(any(Instant.class));
	}
}