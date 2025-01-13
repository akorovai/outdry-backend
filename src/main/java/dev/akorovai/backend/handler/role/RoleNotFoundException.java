package dev.akorovai.backend.handler.role;


import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class RoleNotFoundException extends RuntimeException {
	private final String message;
}
