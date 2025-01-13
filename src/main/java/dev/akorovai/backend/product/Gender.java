package dev.akorovai.backend.product;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Gender {
	MEN("Men"),
	WOMEN("Women"),
	BOYS("Boys"),
	GIRLS("Girls"),
	UNISEX("Unisex");

	private final String displayName;
	@JsonValue
	public String getDisplayName() {
		return displayName;
	}
}