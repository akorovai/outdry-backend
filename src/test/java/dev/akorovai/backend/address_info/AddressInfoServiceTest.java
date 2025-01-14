package dev.akorovai.backend.address_info;

import dev.akorovai.backend.address_info.response.AddressInfoResponse;
import dev.akorovai.backend.security.JwtService;
import dev.akorovai.backend.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Constructor;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AddressInfoService Tests")
class AddressInfoServiceTest {

	@Mock
	private AddressInfoRepository addressInfoRepository;

	@Mock
	private JwtService jwtService;

	@Mock
	private AddressInfoMapper addressInfoMapper;

	@InjectMocks
	private AddressInfoService addressInfoService;

	private User authenticatedUser;

	@BeforeEach
	void setUp() {
		authenticatedUser = createTestUser();
		authenticatedUser.setId(1L);
	}

	private User createTestUser() {
		try {
			Constructor<User> constructor = User.class.getDeclaredConstructor();
			constructor.setAccessible(true);
			return constructor.newInstance();
		} catch ( Exception e ) {
			throw new RuntimeException("Failed to create test User instance", e);
		}
	}

	@Nested
	@DisplayName("Get Addresses for Authenticated User")
	class GetAddressesForAuthenticatedUser {

		@Test
		@DisplayName("Should return list of addresses for authenticated user")
		void shouldReturnListOfAddresses() {
			AddressInfo addressInfo = new AddressInfo();
			AddressInfoResponse addressInfoResponse = new AddressInfoResponse();

			when(jwtService.getAuthenticatedUser()).thenReturn(authenticatedUser);
			when(addressInfoRepository.findByUser(authenticatedUser)).thenReturn(List.of(addressInfo));
			when(addressInfoMapper.toResponse(addressInfo)).thenReturn(addressInfoResponse);

			List<AddressInfoResponse> result = addressInfoService.getAddressesForAuthenticatedUser();

			assertThat(result, contains(addressInfoResponse));
			verify(jwtService).getAuthenticatedUser();
			verify(addressInfoRepository).findByUser(authenticatedUser);
			verify(addressInfoMapper).toResponse(addressInfo);
		}

		@Test
		@DisplayName("Should return empty list if no addresses found")
		void shouldReturnEmptyList() {
			when(jwtService.getAuthenticatedUser()).thenReturn(authenticatedUser);
			when(addressInfoRepository.findByUser(authenticatedUser)).thenReturn(Collections.emptyList());

			List<AddressInfoResponse> result = addressInfoService.getAddressesForAuthenticatedUser();

			assertThat(result, is(empty()));
			verify(jwtService).getAuthenticatedUser();
			verify(addressInfoRepository).findByUser(authenticatedUser);
		}
	}

	@Nested
	@DisplayName("Add Address for Authenticated User")
	class AddAddressForAuthenticatedUser {

		@Test
		@DisplayName("Should add address for authenticated user")
		void shouldAddAddress() {
			AddressInfoResponse addressInfoResponse = new AddressInfoResponse();
			AddressInfo addressInfo = new AddressInfo();
			AddressInfo savedAddressInfo = new AddressInfo();
			AddressInfoResponse savedAddressInfoResponse = new AddressInfoResponse();

			when(jwtService.getAuthenticatedUser()).thenReturn(authenticatedUser);
			when(addressInfoMapper.toEntity(addressInfoResponse)).thenReturn(addressInfo);
			when(addressInfoRepository.save(addressInfo)).thenReturn(savedAddressInfo);
			when(addressInfoMapper.toResponse(savedAddressInfo)).thenReturn(savedAddressInfoResponse);

			AddressInfoResponse result = addressInfoService.addAddressForAuthenticatedUser(addressInfoResponse);

			assertThat(result, is(savedAddressInfoResponse));
			verify(jwtService).getAuthenticatedUser();
			verify(addressInfoMapper).toEntity(addressInfoResponse);
			verify(addressInfoRepository).save(addressInfo);
			verify(addressInfoMapper).toResponse(savedAddressInfo);
		}
	}

	@Nested
	@DisplayName("Edit Address for Authenticated User")
	class EditAddressForAuthenticatedUser {

		@Test
		@DisplayName("Should edit address for authenticated user")
		void shouldEditAddress() {
			Long addressId = 1L;
			AddressInfoResponse addressInfoResponse = new AddressInfoResponse();
			AddressInfo existingAddress = new AddressInfo();
			AddressInfo updatedAddress = new AddressInfo();
			AddressInfoResponse updatedAddressResponse = new AddressInfoResponse();

			when(jwtService.getAuthenticatedUser()).thenReturn(authenticatedUser);
			when(addressInfoRepository.findByIdAndUser(addressId, authenticatedUser)).thenReturn(Optional.of(existingAddress));
			doNothing().when(addressInfoMapper).updateEntityFromDTO(addressInfoResponse, existingAddress);
			when(addressInfoRepository.save(existingAddress)).thenReturn(updatedAddress);
			when(addressInfoMapper.toResponse(updatedAddress)).thenReturn(updatedAddressResponse);

			AddressInfoResponse result = addressInfoService.editAddressForAuthenticatedUser(addressId, addressInfoResponse);

			assertThat(result, is(updatedAddressResponse));
			verify(jwtService).getAuthenticatedUser();
			verify(addressInfoRepository).findByIdAndUser(addressId, authenticatedUser);
			verify(addressInfoMapper).updateEntityFromDTO(addressInfoResponse, existingAddress);
			verify(addressInfoRepository).save(existingAddress);
			verify(addressInfoMapper).toResponse(updatedAddress);
		}

		@Test
		@DisplayName("Should throw exception if address not found or no permission")
		void shouldThrowExceptionIfAddressNotFound() {
			Long addressId = 1L;
			AddressInfoResponse addressInfoResponse = new AddressInfoResponse();

			when(jwtService.getAuthenticatedUser()).thenReturn(authenticatedUser);
			when(addressInfoRepository.findByIdAndUser(addressId, authenticatedUser)).thenReturn(Optional.empty());

			assertThatThrownBy(() -> addressInfoService.editAddressForAuthenticatedUser(addressId, addressInfoResponse)).isInstanceOf(RuntimeException.class).hasMessage("Address not found or you do not have permission to edit it");

			verify(jwtService).getAuthenticatedUser();
			verify(addressInfoRepository).findByIdAndUser(addressId, authenticatedUser);
		}
	}

	@Nested
	@DisplayName("Delete Address for Authenticated User")
	class DeleteAddressForAuthenticatedUser {

		@Test
		@DisplayName("Should delete address for authenticated user")
		void shouldDeleteAddress() {
			Long addressId = 1L;
			AddressInfo addressInfo = new AddressInfo();

			when(jwtService.getAuthenticatedUser()).thenReturn(authenticatedUser);
			when(addressInfoRepository.findByIdAndUser(addressId, authenticatedUser)).thenReturn(Optional.of(addressInfo));

			addressInfoService.deleteAddressForAuthenticatedUser(addressId);

			verify(jwtService).getAuthenticatedUser();
			verify(addressInfoRepository).findByIdAndUser(addressId, authenticatedUser);
			verify(addressInfoRepository).delete(addressInfo);
		}

		@Test
		@DisplayName("Should throw exception if address not found or no permission")
		void shouldThrowExceptionIfAddressNotFound() {
			Long addressId = 1L;

			when(jwtService.getAuthenticatedUser()).thenReturn(authenticatedUser);
			when(addressInfoRepository.findByIdAndUser(addressId, authenticatedUser)).thenReturn(Optional.empty());

			assertThatThrownBy(() -> addressInfoService.deleteAddressForAuthenticatedUser(addressId)).isInstanceOf(RuntimeException.class).hasMessage("Address not found or you do not have permission to delete it");

			verify(jwtService).getAuthenticatedUser();
			verify(addressInfoRepository).findByIdAndUser(addressId, authenticatedUser);
		}
	}
}
