package dev.akorovai.backend.handler;

import com.github.javafaker.Faker;
import dev.akorovai.backend.handler.azure.FileTypeException;
import dev.akorovai.backend.handler.email.EmailSendingException;
import dev.akorovai.backend.handler.email.EmailTemplateException;
import dev.akorovai.backend.handler.general.UniqueConstraintViolationException;
import dev.akorovai.backend.handler.order.NoOrdersFoundException;
import dev.akorovai.backend.handler.product.ProductNotFoundException;
import dev.akorovai.backend.handler.refresh_token.TokenExpiredException;
import dev.akorovai.backend.handler.refresh_token.TokenNotFoundException;
import dev.akorovai.backend.handler.refresh_token.TokenRefreshException;
import dev.akorovai.backend.handler.role.RoleNotFoundException;
import dev.akorovai.backend.handler.shopping_cart.InsufficientStockException;
import dev.akorovai.backend.handler.shopping_cart.ShoppingCartItemNotFoundException;
import dev.akorovai.backend.handler.shopping_cart.UnauthorizedItemDeletionException;
import dev.akorovai.backend.handler.shopping_cart.UnauthorizedItemModificationException;
import dev.akorovai.backend.handler.user.UserNotFoundException;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationContextException;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.springframework.http.HttpStatus.*;

@ExtendWith(MockitoExtension.class)
class ExceptionStatusTest {

	private Faker faker;

	@BeforeEach
	void setUp() {
		faker = new Faker();
	}

	@Test
	void testGetStatusFor_UserNotFoundException() {
		// Arrange
		Throwable exception = new UserNotFoundException(faker.lorem().sentence());

		// Act
		HttpStatus status = ExceptionStatus.getStatusFor(exception);

		// Assert using AssertJ
		assertThat(status).isEqualTo(NOT_FOUND);

		// Assert using Hamcrest
		assertThat(status, is(NOT_FOUND));
	}

	@Test
	void testGetStatusFor_RoleNotFoundException() {
		// Arrange
		Throwable exception = new RoleNotFoundException(faker.lorem().sentence());

		// Act
		HttpStatus status = ExceptionStatus.getStatusFor(exception);

		// Assert using AssertJ
		assertThat(status).isEqualTo(NOT_FOUND);

		// Assert using Hamcrest
		assertThat(status, is(NOT_FOUND));
	}

	@Test
	void testGetStatusFor_TokenNotFoundException() {
		// Arrange
		Throwable exception = new TokenNotFoundException(faker.lorem().sentence());

		// Act
		HttpStatus status = ExceptionStatus.getStatusFor(exception);

		// Assert using AssertJ
		assertThat(status).isEqualTo(NOT_FOUND);

		// Assert using Hamcrest
		assertThat(status, is(NOT_FOUND));
	}

	@Test
	void testGetStatusFor_NoOrdersFoundException() {
		// Arrange
		Throwable exception = new NoOrdersFoundException(faker.lorem().sentence());

		// Act
		HttpStatus status = ExceptionStatus.getStatusFor(exception);

		// Assert using AssertJ
		assertThat(status).isEqualTo(NOT_FOUND);

		// Assert using Hamcrest
		assertThat(status, is(NOT_FOUND));
	}

	@Test
	void testGetStatusFor_ProductNotFoundException() {
		// Arrange
		Throwable exception = new ProductNotFoundException(faker.lorem().sentence());

		// Act
		HttpStatus status = ExceptionStatus.getStatusFor(exception);

		// Assert using AssertJ
		assertThat(status).isEqualTo(NOT_FOUND);

		// Assert using Hamcrest
		assertThat(status, is(NOT_FOUND));
	}

	@Test
	void testGetStatusFor_ShoppingCartItemNotFoundException() {
		// Arrange
		Throwable exception = new ShoppingCartItemNotFoundException(faker.lorem().sentence());

		// Act
		HttpStatus status = ExceptionStatus.getStatusFor(exception);

		// Assert using AssertJ
		assertThat(status).isEqualTo(NOT_FOUND);

		// Assert using Hamcrest
		assertThat(status, is(NOT_FOUND));
	}


	@Test
	void testGetStatusFor_ConstraintViolationException() {
		// Arrange
		Throwable exception = new ConstraintViolationException(faker.lorem().sentence(), null);

		// Act
		HttpStatus status = ExceptionStatus.getStatusFor(exception);

		// Assert using AssertJ
		assertThat(status).isEqualTo(BAD_REQUEST);

		// Assert using Hamcrest
		assertThat(status, is(BAD_REQUEST));
	}

	@Test
	void testGetStatusFor_TokenRefreshException() {
		// Arrange
		Throwable exception = new TokenRefreshException(faker.lorem().sentence());

		// Act
		HttpStatus status = ExceptionStatus.getStatusFor(exception);

		// Assert using AssertJ
		assertThat(status).isEqualTo(BAD_REQUEST);

		// Assert using Hamcrest
		assertThat(status, is(BAD_REQUEST));
	}

	@Test
	void testGetStatusFor_TokenExpiredException() {
		// Arrange
		Throwable exception = new TokenExpiredException(faker.lorem().sentence());

		// Act
		HttpStatus status = ExceptionStatus.getStatusFor(exception);

		// Assert using AssertJ
		assertThat(status).isEqualTo(BAD_REQUEST);

		// Assert using Hamcrest
		assertThat(status, is(BAD_REQUEST));
	}

	@Test
	void testGetStatusFor_InsufficientStockException() {
		// Arrange
		Throwable exception = new InsufficientStockException(faker.lorem().sentence());

		// Act
		HttpStatus status = ExceptionStatus.getStatusFor(exception);

		// Assert using AssertJ
		assertThat(status).isEqualTo(BAD_REQUEST);

		// Assert using Hamcrest
		assertThat(status, is(BAD_REQUEST));
	}

	@Test
	void testGetStatusFor_FileTypeException() {
		// Arrange
		Throwable exception = new FileTypeException(faker.lorem().sentence());

		// Act
		HttpStatus status = ExceptionStatus.getStatusFor(exception);

		// Assert using AssertJ
		assertThat(status).isEqualTo(BAD_REQUEST);

		// Assert using Hamcrest
		assertThat(status, is(BAD_REQUEST));
	}

	@Test
	void testGetStatusFor_BadCredentialsException() {
		// Arrange
		Throwable exception = new BadCredentialsException(faker.lorem().sentence());

		// Act
		HttpStatus status = ExceptionStatus.getStatusFor(exception);

		// Assert using AssertJ
		assertThat(status).isEqualTo(UNAUTHORIZED);

		// Assert using Hamcrest
		assertThat(status, is(UNAUTHORIZED));
	}

	@Test
	void testGetStatusFor_UnauthorizedItemDeletionException() {
		// Arrange
		Throwable exception = new UnauthorizedItemDeletionException(faker.lorem().sentence());

		// Act
		HttpStatus status = ExceptionStatus.getStatusFor(exception);

		// Assert using AssertJ
		assertThat(status).isEqualTo(UNAUTHORIZED);

		// Assert using Hamcrest
		assertThat(status, is(UNAUTHORIZED));
	}

	@Test
	void testGetStatusFor_UnauthorizedItemModificationException() {
		// Arrange
		Throwable exception = new UnauthorizedItemModificationException(faker.lorem().sentence());

		// Act
		HttpStatus status = ExceptionStatus.getStatusFor(exception);

		// Assert using AssertJ
		assertThat(status).isEqualTo(UNAUTHORIZED);

		// Assert using Hamcrest
		assertThat(status, is(UNAUTHORIZED));
	}

	@Test
	void testGetStatusFor_UniqueConstraintViolationException() {
		// Arrange
		Throwable exception = new UniqueConstraintViolationException(faker.lorem().sentence(), "baby");

		// Act
		HttpStatus status = ExceptionStatus.getStatusFor(exception);

		// Assert using AssertJ
		assertThat(status).isEqualTo(CONFLICT);

		// Assert using Hamcrest
		assertThat(status, is(CONFLICT));
	}

	@Test
	void testGetStatusFor_EmailTemplateException() {
		// Arrange
		Throwable exception = new EmailTemplateException(faker.lorem().sentence());

		// Act
		HttpStatus status = ExceptionStatus.getStatusFor(exception);

		// Assert using AssertJ
		assertThat(status).isEqualTo(INTERNAL_SERVER_ERROR);

		// Assert using Hamcrest
		assertThat(status, is(INTERNAL_SERVER_ERROR));
	}

	@Test
	void testGetStatusFor_EmailSendingException() {
		// Arrange
		Throwable exception = new EmailSendingException(faker.lorem().sentence());

		// Act
		HttpStatus status = ExceptionStatus.getStatusFor(exception);

		// Assert using AssertJ
		assertThat(status).isEqualTo(INTERNAL_SERVER_ERROR);

		// Assert using Hamcrest
		assertThat(status, is(INTERNAL_SERVER_ERROR));
	}

	@Test
	void testGetStatusFor_GenericException() {
		// Arrange
		Throwable exception = new Exception(faker.lorem().sentence());

		// Act
		HttpStatus status = ExceptionStatus.getStatusFor(exception);

		// Assert using AssertJ
		assertThat(status).isEqualTo(INTERNAL_SERVER_ERROR);

		// Assert using Hamcrest
		assertThat(status, is(INTERNAL_SERVER_ERROR));
	}

	@Test
	void testGetStatusFor_InternalError() {
		// Arrange
		Throwable exception = new InternalError(faker.lorem().sentence());

		// Act
		HttpStatus status = ExceptionStatus.getStatusFor(exception);

		// Assert using AssertJ
		assertThat(status).isEqualTo(INTERNAL_SERVER_ERROR);

		// Assert using Hamcrest
		assertThat(status, is(INTERNAL_SERVER_ERROR));
	}

	@Test
	void testGetStatusFor_ApplicationContextException() {
		// Arrange
		Throwable exception = new ApplicationContextException(faker.lorem().sentence());

		// Act
		HttpStatus status = ExceptionStatus.getStatusFor(exception);

		// Assert using AssertJ
		assertThat(status).isEqualTo(INTERNAL_SERVER_ERROR);

		// Assert using Hamcrest
		assertThat(status, is(INTERNAL_SERVER_ERROR));
	}
}