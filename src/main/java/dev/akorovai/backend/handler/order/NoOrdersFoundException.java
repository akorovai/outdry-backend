package dev.akorovai.backend.handler.order;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class NoOrdersFoundException extends RuntimeException {
	private final String message;
}
