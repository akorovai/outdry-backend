package dev.akorovai.backend.handler.user;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class UserNotFoundException extends RuntimeException {
	private final String message;
}
