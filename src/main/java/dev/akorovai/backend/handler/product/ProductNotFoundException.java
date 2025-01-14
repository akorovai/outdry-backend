package dev.akorovai.backend.handler.product;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class ProductNotFoundException extends RuntimeException {
	private final String message;
}
