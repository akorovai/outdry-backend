package dev.akorovai.backend.security.validation;

import com.github.javafaker.Faker;
import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PasswordValidatorTest {

	@Mock
	private ConstraintValidatorContext context;

	@Mock
	private ConstraintValidatorContext.ConstraintViolationBuilder builder;

	private PasswordValidator passwordValidator;
	private Faker faker;

	@BeforeEach
	void setUp() {
		passwordValidator = new PasswordValidator();
		faker = new Faker();
	}

	@Test
	void isValid_ShouldReturnTrueForValidPassword() {
		// Arrange
		ValidPassword validPassword = mock(ValidPassword.class);
		when(validPassword.min()).thenReturn(8);
		when(validPassword.max()).thenReturn(20);
		passwordValidator.initialize(validPassword);

		String password = "ValidPass123!";

		// Act
		boolean isValid = passwordValidator.isValid(password, context);

		// Assert
		assertThat(isValid).isTrue();
		verifyNoInteractions(context); // No validation errors should be reported
	}

	@Test
	void isValid_ShouldReturnFalseForNullPassword() {
		// Arrange
		ValidPassword validPassword = mock(ValidPassword.class);
		when(validPassword.min()).thenReturn(8);
		when(validPassword.max()).thenReturn(20);
		passwordValidator.initialize(validPassword);

		// Act
		boolean isValid = passwordValidator.isValid(null, context);

		// Assert
		assertThat(isValid).isFalse();
		verifyNoInteractions(context); // No validation errors should be reported
	}

	@Test
	void isValid_ShouldReturnFalseForPasswordWithoutUppercase() {
		// Arrange
		ValidPassword validPassword = mock(ValidPassword.class);
		when(validPassword.min()).thenReturn(8);
		when(validPassword.max()).thenReturn(20);
		passwordValidator.initialize(validPassword);

		String password = "invalidpass123!";

		// Act
		boolean isValid = passwordValidator.isValid(password, context);

		// Assert
		assertThat(isValid).isFalse();
		verifyNoInteractions(context); // No validation errors should be reported
	}

	@Test
	void isValid_ShouldReturnFalseForPasswordWithoutLowercase() {
		// Arrange
		ValidPassword validPassword = mock(ValidPassword.class);
		when(validPassword.min()).thenReturn(8);
		when(validPassword.max()).thenReturn(20);
		passwordValidator.initialize(validPassword);

		String password = "INVALIDPASS123!";

		// Act
		boolean isValid = passwordValidator.isValid(password, context);

		// Assert
		assertThat(isValid).isFalse();
		verifyNoInteractions(context); // No validation errors should be reported
	}

	@Test
	void isValid_ShouldReturnFalseForPasswordWithoutSpecialCharacter() {
		// Arrange
		ValidPassword validPassword = mock(ValidPassword.class);
		when(validPassword.min()).thenReturn(8);
		when(validPassword.max()).thenReturn(20);
		passwordValidator.initialize(validPassword);

		String password = "InvalidPass123";

		// Act
		boolean isValid = passwordValidator.isValid(password, context);

		// Assert
		assertThat(isValid).isFalse();
		verifyNoInteractions(context); // No validation errors should be reported
	}

	@Test
	void isValid_ShouldReturnFalseForPasswordTooShort() {
		// Arrange
		ValidPassword validPassword = mock(ValidPassword.class);
		when(validPassword.min()).thenReturn(8);
		when(validPassword.max()).thenReturn(20);
		passwordValidator.initialize(validPassword);

		String password = "Short1!";

		// Act
		boolean isValid = passwordValidator.isValid(password, context);

		// Assert
		assertThat(isValid).isFalse();
		verifyNoInteractions(context); // No validation errors should be reported
	}

	@Test
	void isValid_ShouldReturnFalseForPasswordTooLong() {
		// Arrange
		ValidPassword validPassword = mock(ValidPassword.class);
		when(validPassword.min()).thenReturn(8);
		when(validPassword.max()).thenReturn(20);
		passwordValidator.initialize(validPassword);

		String password = "ThisPasswordIsWayTooLong123!";

		// Act
		boolean isValid = passwordValidator.isValid(password, context);

		// Assert
		assertThat(isValid).isFalse();
		verifyNoInteractions(context); // No validation errors should be reported
	}

	@Test
	void isValid_ShouldReturnTrueForPasswordWithExactMinLength() {
		// Arrange
		ValidPassword validPassword = mock(ValidPassword.class);
		when(validPassword.min()).thenReturn(8);
		when(validPassword.max()).thenReturn(20);
		passwordValidator.initialize(validPassword);

		String password = "Valid1!@";

		// Act
		boolean isValid = passwordValidator.isValid(password, context);

		// Assert
		assertThat(isValid).isTrue();
		verifyNoInteractions(context); // No validation errors should be reported
	}

	@Test
	void isValid_ShouldReturnTrueForPasswordWithExactMaxLength() {
		// Arrange
		ValidPassword validPassword = mock(ValidPassword.class);
		when(validPassword.min()).thenReturn(8);
		when(validPassword.max()).thenReturn(20);
		passwordValidator.initialize(validPassword);

		String password = "ValidPassword123!@#";

		// Act
		boolean isValid = passwordValidator.isValid(password, context);

		// Assert
		assertThat(isValid).isTrue();
		verifyNoInteractions(context); // No validation errors should be reported
	}

	@Test
	void isValid_ShouldReturnFalseForPasswordWithInvalidCharacters() {
		// Arrange
		ValidPassword validPassword = mock(ValidPassword.class);
		when(validPassword.min()).thenReturn(8);
		when(validPassword.max()).thenReturn(20);
		passwordValidator.initialize(validPassword);

		String password = "InvalidPassword123"; // Missing special character

		// Act
		boolean isValid = passwordValidator.isValid(password, context);

		// Assert
		assertThat(isValid).isFalse();
		verifyNoInteractions(context); // No validation errors should be reported
	}

	@Test
	void isValid_ShouldReturnTrueForRandomValidPassword() {
		// Arrange
		ValidPassword validPassword = mock(ValidPassword.class);
		when(validPassword.min()).thenReturn(8);
		when(validPassword.max()).thenReturn(20);
		passwordValidator.initialize(validPassword);

		// Generate a random password that meets all criteria
		String password = faker.internet().password(8, 20, true, true, true);

		// Ensure the password contains at least one special character
		password = password.replaceFirst("[^!@#$%^&*()_+\\-=\\[\\]{}|;':\",.<>?/`~]", "!");

		// Act
		boolean isValid = passwordValidator.isValid(password, context);

		// Assert
		assertThat(isValid).isTrue();
		verifyNoInteractions(context); // No validation errors should be reported
	}

	@Test
	void isValid_ShouldReturnFalseForPasswordWithOnlyUppercase() {
		// Arrange
		ValidPassword validPassword = mock(ValidPassword.class);
		when(validPassword.min()).thenReturn(8);
		when(validPassword.max()).thenReturn(20);
		passwordValidator.initialize(validPassword);

		String password = "INVALIDPASS";

		// Act
		boolean isValid = passwordValidator.isValid(password, context);

		// Assert
		assertThat(isValid).isFalse();
		verifyNoInteractions(context); // No validation errors should be reported
	}

	@Test
	void isValid_ShouldReturnFalseForPasswordWithOnlyLowercase() {
		// Arrange
		ValidPassword validPassword = mock(ValidPassword.class);
		when(validPassword.min()).thenReturn(8);
		when(validPassword.max()).thenReturn(20);
		passwordValidator.initialize(validPassword);

		String password = "invalidpass";

		// Act
		boolean isValid = passwordValidator.isValid(password, context);

		// Assert
		assertThat(isValid).isFalse();
		verifyNoInteractions(context); // No validation errors should be reported
	}

	@Test
	void isValid_ShouldReturnFalseForPasswordWithOnlyNumbers() {
		// Arrange
		ValidPassword validPassword = mock(ValidPassword.class);
		when(validPassword.min()).thenReturn(8);
		when(validPassword.max()).thenReturn(20);
		passwordValidator.initialize(validPassword);

		String password = "12345678";

		// Act
		boolean isValid = passwordValidator.isValid(password, context);

		// Assert
		assertThat(isValid).isFalse();
		verifyNoInteractions(context); // No validation errors should be reported
	}

	@Test
	void isValid_ShouldReturnFalseForPasswordWithOnlySpecialCharacters() {
		// Arrange
		ValidPassword validPassword = mock(ValidPassword.class);
		when(validPassword.min()).thenReturn(8);
		when(validPassword.max()).thenReturn(20);
		passwordValidator.initialize(validPassword);

		String password = "!@#$%^&*";

		// Act
		boolean isValid = passwordValidator.isValid(password, context);

		// Assert
		assertThat(isValid).isFalse();
		verifyNoInteractions(context); // No validation errors should be reported
	}

	@Test
	void isValid_ShouldReturnFalseForPasswordWithWhitespace() {
		// Arrange
		ValidPassword validPassword = mock(ValidPassword.class);
		when(validPassword.min()).thenReturn(8);
		when(validPassword.max()).thenReturn(20);
		passwordValidator.initialize(validPassword);

		String password = "Invalid Pass123!";

		// Act
		boolean isValid = passwordValidator.isValid(password, context);

		// Assert
		assertThat(isValid).isFalse();
		verifyNoInteractions(context); // No validation errors should be reported
	}

	@Test
	void isValid_ShouldReturnFalseForPasswordWithUnicodeCharacters() {
		// Arrange
		ValidPassword validPassword = mock(ValidPassword.class);
		when(validPassword.min()).thenReturn(8);
		when(validPassword.max()).thenReturn(20);
		passwordValidator.initialize(validPassword);

		String password = "InvalidPass123!😊";

		// Act
		boolean isValid = passwordValidator.isValid(password, context);

		// Assert
		assertThat(isValid).isFalse();
		verifyNoInteractions(context); // No validation errors should be reported
	}

}