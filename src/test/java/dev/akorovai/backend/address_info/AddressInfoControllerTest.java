package dev.akorovai.backend.address_info;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.akorovai.backend.address_info.response.AddressInfoResponse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import static org.mockito.ArgumentMatchers.any;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AddressInfoController Tests")
class AddressInfoControllerTest {

	@Mock
	private AddressInfoService addressInfoService;

	@InjectMocks
	private AddressInfoController addressInfoController;

	private MockMvc mockMvc;
	private ObjectMapper objectMapper;

	@BeforeEach
	void setUp() {
		mockMvc = MockMvcBuilders.standaloneSetup(addressInfoController).build();
		objectMapper = new ObjectMapper();
	}

	@Nested
	@DisplayName("GET /api/addresses")
	class GetAddresses {

		@Test
		@DisplayName("Should return list of addresses")
		void shouldReturnListOfAddresses() throws Exception {
			AddressInfoResponse address1 = AddressInfoResponse.builder()
					                               .id(1L).fullName("John Doe").state("CA").city("Los Angeles").build();
			AddressInfoResponse address2 = AddressInfoResponse.builder()
					                               .id(2L).fullName("Jane Smith").state("NY").city("New York").build();

			when(addressInfoService.getAddressesForAuthenticatedUser()).thenReturn(List.of(address1, address2));

			mockMvc.perform(get("/api/addresses"))
					.andExpect(status().isOk())
					.andExpect(content().contentType(MediaType.APPLICATION_JSON))
					.andExpect(jsonPath("$.code", is(HttpStatus.OK.value())))
					.andExpect(jsonPath("$.message", hasSize(2)))
					.andExpect(jsonPath("$.message[0].fullName", is("John Doe")))
					.andExpect(jsonPath("$.message[1].city", is("New York")));

			verify(addressInfoService).getAddressesForAuthenticatedUser();
		}
	}

	@Nested
	@DisplayName("POST /api/addresses")
	class AddAddress {

		@Test
		@DisplayName("Should add a new address")
		void shouldAddNewAddress() throws Exception {
			AddressInfoResponse request = AddressInfoResponse.builder()
					                              .fullName("John Doe").state("CA").city("Los Angeles").phone("123456789").build();
			AddressInfoResponse savedAddress = AddressInfoResponse.builder()
					                                   .id(1L).fullName("John Doe").state("CA").city("Los Angeles").phone("123456789").build();

			when(addressInfoService.addAddressForAuthenticatedUser(any(AddressInfoResponse.class))).thenReturn(savedAddress);

			mockMvc.perform(post("/api/addresses")
					                .contentType(MediaType.APPLICATION_JSON)
					                .content(objectMapper.writeValueAsString(request)))
					.andExpect(status().isOk())
					.andExpect(content().contentType(MediaType.APPLICATION_JSON))
					.andExpect(jsonPath("$.code", is(HttpStatus.OK.value())))
					.andExpect(jsonPath("$.message.id", is(1)))
					.andExpect(jsonPath("$.message.fullName", is("John Doe")));

			verify(addressInfoService).addAddressForAuthenticatedUser(any(AddressInfoResponse.class));
		}
	}

	@Nested
	@DisplayName("PUT /api/addresses/{addressId}")
	class EditAddress {

		@Test
		@DisplayName("Should edit an address")
		void shouldEditAddress() throws Exception {
			Long addressId = 1L;
			AddressInfoResponse request = AddressInfoResponse.builder()
					                              .fullName("John Updated").state("CA").city("Los Angeles").phone("987654321").build();
			AddressInfoResponse updatedAddress = AddressInfoResponse.builder()
					                                     .id(addressId).fullName("John Updated").state("CA").city("Los Angeles").phone("987654321").build();

			when(addressInfoService.editAddressForAuthenticatedUser(eq(addressId), any(AddressInfoResponse.class)))
					.thenReturn(updatedAddress);

			mockMvc.perform(put("/api/addresses/{addressId}", addressId)
					                .contentType(MediaType.APPLICATION_JSON)
					                .content(objectMapper.writeValueAsString(request)))
					.andExpect(status().isOk())
					.andExpect(content().contentType(MediaType.APPLICATION_JSON))
					.andExpect(jsonPath("$.code", is(HttpStatus.OK.value())))
					.andExpect(jsonPath("$.message.id", is(1)))
					.andExpect(jsonPath("$.message.fullName", is("John Updated")));

			verify(addressInfoService).editAddressForAuthenticatedUser(eq(addressId), any(AddressInfoResponse.class));
		}
	}

	@Nested
	@DisplayName("DELETE /api/addresses/{addressId}")
	class DeleteAddress {

		@Test
		@DisplayName("Should delete an address")
		void shouldDeleteAddress() throws Exception {
			Long addressId = 1L;

			doNothing().when(addressInfoService).deleteAddressForAuthenticatedUser(addressId);

			mockMvc.perform(delete("/api/addresses/{addressId}", addressId))
					.andExpect(status().isNoContent())
					.andExpect(jsonPath("$.code", is(HttpStatus.NO_CONTENT.value())))
					.andExpect(jsonPath("$.message", is("Address deleted successfully")));

			verify(addressInfoService).deleteAddressForAuthenticatedUser(addressId);
		}
	}
}
