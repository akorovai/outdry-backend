package dev.akorovai.backend.handler;

import com.github.javafaker.Faker;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

	@InjectMocks
	private GlobalExceptionHandler globalExceptionHandler;

	private Faker faker;

	@BeforeEach
	void setUp() {
		faker = new Faker();
	}

	@Test
	void testHandleMethodArgumentNotValidException() {
		// Arrange
		MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
		BindingResult bindingResult = mock(BindingResult.class);
		FieldError fieldError = new FieldError("objectName", "fieldName", "error message");

		when(ex.getBindingResult()).thenReturn(bindingResult);
		when(bindingResult.getFieldErrors()).thenReturn(Collections.singletonList(fieldError));

		// Act
		ResponseEntity<GlobalExceptionHandler.ErrorResponse> response = globalExceptionHandler.handleAllThrowables(ex);

		// Assert using AssertJ
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
		assertThat(response.getBody()).isNotNull();
		assertThat(response.getBody().errorCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
		assertThat(response.getBody().message()).isEqualTo("Validation failed: fieldName: error message");

		// Assert using Hamcrest
		assertThat(response.getStatusCode(), is(HttpStatus.BAD_REQUEST));
		assertThat(response.getBody(), is(notNullValue()));
		assertThat(response.getBody().errorCode(), is(HttpStatus.BAD_REQUEST.value()));
		assertThat(response.getBody().message(), is("Validation failed: fieldName: error message"));
	}

	@Test
	void testHandleConstraintViolationException() {
		// Arrange
		ConstraintViolationException ex = mock(ConstraintViolationException.class);
		ConstraintViolation<?> violation = mock(ConstraintViolation.class);
		Path path = mock(Path.class);

		when(ex.getConstraintViolations()).thenReturn(Set.of(violation));
		when(violation.getPropertyPath()).thenReturn(path);
		when(path.toString()).thenReturn("fieldName");
		when(violation.getMessage()).thenReturn("error message");

		// Act
		ResponseEntity<GlobalExceptionHandler.ErrorResponse> response = globalExceptionHandler.handleAllThrowables(ex);

		// Assert using AssertJ
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
		assertThat(response.getBody()).isNotNull();
		assertThat(response.getBody().errorCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
		assertThat(response.getBody().message()).isEqualTo("Validation failed: fieldName: error message");

		// Assert using Hamcrest
		assertThat(response.getStatusCode(), is(HttpStatus.BAD_REQUEST));
		assertThat(response.getBody(), is(notNullValue()));
		assertThat(response.getBody().errorCode(), is(HttpStatus.BAD_REQUEST.value()));
		assertThat(response.getBody().message(), is("Validation failed: fieldName: error message"));
	}

	@Test
	void testHandleGenericException() {
		// Arrange
		Throwable throwable = new RuntimeException("Generic error message");

		// Act
		ResponseEntity<GlobalExceptionHandler.ErrorResponse> response = globalExceptionHandler.handleAllThrowables(throwable);

		// Assert using AssertJ
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
		assertThat(response.getBody()).isNotNull();
		assertThat(response.getBody().errorCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
		assertThat(response.getBody().message()).isEqualTo("Generic error message");

		// Assert using Hamcrest
		assertThat(response.getStatusCode(), is(HttpStatus.INTERNAL_SERVER_ERROR));
		assertThat(response.getBody(), is(notNullValue()));
		assertThat(response.getBody().errorCode(), is(HttpStatus.INTERNAL_SERVER_ERROR.value()));
		assertThat(response.getBody().message(), is("Generic error message"));
	}

	@Test
	void testHandleExceptionWithRootCause() {
		// Arrange
		Throwable rootCause = new RuntimeException("Root cause error message");
		Throwable throwable = new RuntimeException("Wrapper error message", rootCause);

		// Act
		ResponseEntity<GlobalExceptionHandler.ErrorResponse> response = globalExceptionHandler.handleAllThrowables(throwable);

		// Assert using AssertJ
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
		assertThat(response.getBody()).isNotNull();
		assertThat(response.getBody().errorCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
		assertThat(response.getBody().message()).isEqualTo("Root cause error message");

		// Assert using Hamcrest
		assertThat(response.getStatusCode(), is(HttpStatus.INTERNAL_SERVER_ERROR));
		assertThat(response.getBody(), is(notNullValue()));
		assertThat(response.getBody().errorCode(), is(HttpStatus.INTERNAL_SERVER_ERROR.value()));
		assertThat(response.getBody().message(), is("Root cause error message"));
	}
}