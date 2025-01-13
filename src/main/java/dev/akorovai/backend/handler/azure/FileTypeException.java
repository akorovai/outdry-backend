package dev.akorovai.backend.handler.azure;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class FileTypeException extends IllegalArgumentException {
	private final String message;
}
