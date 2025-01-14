package dev.akorovai.backend.handler;


import dev.akorovai.backend.handler.azure.FileTypeException;
import dev.akorovai.backend.handler.email.EmailSendingException;
import dev.akorovai.backend.handler.email.EmailTemplateException;
import dev.akorovai.backend.handler.general.UniqueConstraintViolationException;
import dev.akorovai.backend.handler.order.NoOrdersFoundException;
import dev.akorovai.backend.handler.product.ProductNotFoundException;
import dev.akorovai.backend.handler.refresh_token.TokenExpiredException;
import dev.akorovai.backend.handler.refresh_token.TokenNotFoundException;
import dev.akorovai.backend.handler.refresh_token.TokenRefreshException;
import dev.akorovai.backend.handler.shopping_cart.InsufficientStockException;
import dev.akorovai.backend.handler.shopping_cart.ShoppingCartItemNotFoundException;
import dev.akorovai.backend.handler.shopping_cart.UnauthorizedItemDeletionException;
import dev.akorovai.backend.handler.shopping_cart.UnauthorizedItemModificationException;
import dev.akorovai.backend.handler.user.UserNotFoundException;
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
	USER_NOT_FOUND(UserNotFoundException.class, NOT_FOUND),
	ROLE_NOT_FOUND(RoleNotFoundException.class, NOT_FOUND),
	TOKEN_NOT_FOUND(TokenNotFoundException.class, NOT_FOUND),
	ORDERS_NOT_FOUND(NoOrdersFoundException.class, NOT_FOUND),
	PRODUCT_NOT_FOUND(ProductNotFoundException.class, NOT_FOUND),
	SHOPPING_CART_ITEM_NOT_FOUND(ShoppingCartItemNotFoundException.class, NOT_FOUND),

	// BAD_REQUEST
	METHOD_ARGUMENT_NOT_VALID(MethodArgumentNotValidException.class, BAD_REQUEST),
	CONSTRAINT_VIOLATION(ConstraintViolationException.class, BAD_REQUEST),

	TOKEN_REFRESH(TokenRefreshException.class, BAD_REQUEST),
	TOKEN_EXPIRED(TokenExpiredException.class, BAD_REQUEST),
	INSUFFICIENT_STOCK(InsufficientStockException.class, BAD_REQUEST),


	FILE_TYPE_EXCEPTION(FileTypeException.class, BAD_REQUEST),
	// UNAUTHORIZED
	BAD_CREDENTIALS(BadCredentialsException.class, UNAUTHORIZED),
	UNAUTHORIZED_ITEM_DELETION(UnauthorizedItemDeletionException.class, UNAUTHORIZED),
	UNAUTHORIZED_ITEM_MODIFICATION(UnauthorizedItemModificationException.class, UNAUTHORIZED),
	// CONFLICT
	UNIQUE_CONSTRAINT_VIOLATION(UniqueConstraintViolationException.class, CONFLICT),

	// INTERNAL_SERVER_ERROR
	EMAIL_TEMPLATE(EmailTemplateException.class, INTERNAL_SERVER_ERROR),
	EMAIL_SENDING(EmailSendingException.class, INTERNAL_SERVER_ERROR),
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