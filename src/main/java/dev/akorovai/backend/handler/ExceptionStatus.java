package dev.akorovai.backend.handler;


import jakarta.validation.ConstraintViolationException;
import org.springframework.context.ApplicationContextException;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;

import javax.management.relation.RoleNotFoundException;
import java.util.IdentityHashMap;
import java.util.Map;


import static org.springframework.http.HttpStatus.*;

public enum ExceptionStatus {
	// NOT_FOUND


	// BAD_REQUEST
	METHOD_ARGUMENT_NOT_VALID(MethodArgumentNotValidException.class, BAD_REQUEST),
	CONSTRAINT_VIOLATION(ConstraintViolationException.class, BAD_REQUEST),



	// UNAUTHORIZED
	BAD_CREDENTIALS(BadCredentialsException.class, UNAUTHORIZED),

	// CONFLICT

	// INTERNAL_SERVER_ERROR

	GENERIC_EXCEPTION(Exception.class, INTERNAL_SERVER_ERROR),

	INTERNAL_ERROR(InternalError.class, INTERNAL_SERVER_ERROR),
	APPLICATION_CONTEXT_EXCEPTION(ApplicationContextException.class, INTERNAL_SERVER_ERROR);

	private static final Map<Class<? extends Throwable>, HttpStatus> EXCEPTION_STATUS_MAP;

	static {
		EXCEPTION_STATUS_MAP = new IdentityHashMap<>(values().length);
		for (ExceptionStatus status : values()) {
			EXCEPTION_STATUS_MAP.put(status.exceptionClass, status.status);
		}
	}

	private final Class<? extends Throwable> exceptionClass;
	private final HttpStatus status;

	ExceptionStatus(Class<? extends Throwable> exceptionClass, HttpStatus status) {
		this.exceptionClass = exceptionClass;
		this.status = status;
	}

	public static HttpStatus getStatusFor(Throwable throwable) {
		Class<?> exceptionClass = throwable.getClass();
		HttpStatus status;
		do {
			status = EXCEPTION_STATUS_MAP.get(exceptionClass);
			if (status != null) {
				return status;
			}
			exceptionClass = exceptionClass.getSuperclass();
		} while (exceptionClass != null);
		return INTERNAL_SERVER_ERROR;
	}
}