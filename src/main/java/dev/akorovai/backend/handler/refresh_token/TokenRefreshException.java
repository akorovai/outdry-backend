package dev.akorovai.backend.handler.refresh_token;
import lombok.Data;
import lombok.EqualsAndHashCode;
@Data
@EqualsAndHashCode(callSuper=false)
public class TokenRefreshException extends RuntimeException {
	private final String message;
}
