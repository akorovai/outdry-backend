package dev.akorovai.backend.address_info.response;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddressInfoResponse {

	@NotNull(message = "ID is required")
	private Long id;

	@NotBlank(message = "Full name is required")
	@Size(max = 128, message = "Full name must be less than or equal to 128 characters")
	private String fullName;

	@NotBlank(message = "State is required")
	@Size(max = 64, message = "State must be less than or equal to 64 characters")
	private String state;

	@NotBlank(message = "Street is required")
	@Size(max = 64, message = "Street must be less than or equal to 64 characters")
	private String street;

	@NotBlank(message = "Apartment is required")
	@Size(max = 64, message = "Apartment must be less than or equal to 64 characters")
	private String apartment;

	@NotBlank(message = "Postal code is required")
	@Size(max = 64, message = "Postal code must be less than or equal to 64 characters")
	@Pattern(regexp = "^[0-9]{2}(?:-[0-9]{3})?$", message = "Postal code must be in a valid " +
			                                                        "format (e.g., 12345 or 12345-6789)")
	private String postalCode;

	@NotBlank(message = "City is required")
	@Size(max = 64, message = "City must be less than or equal to 64 characters")
	private String city;

	@NotBlank(message = "Phone is required")
	@Size(max = 9, message = "Phone must be less than or equal to 9 characters")
	@Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Phone must be in a valid format (e.g., 123456789 or 123456789)")
	private String phone;

	@NotNull(message = "User ID is required")
	private Long userId;
}