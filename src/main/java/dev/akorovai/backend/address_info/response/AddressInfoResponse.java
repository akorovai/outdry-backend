package dev.akorovai.backend.address_info.response;


import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddressInfoResponse{
	private Long id;
	private String fullName;
	private String state;
	private String street;
	private String apartment;
	private String postalCode;
	private String city;
	private String phone;
	private Long userId;
}