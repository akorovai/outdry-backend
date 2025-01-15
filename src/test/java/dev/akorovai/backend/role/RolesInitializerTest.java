package dev.akorovai.backend.role;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class RolesInitializerTest {

	@Mock
	private RoleRepository repository;

	@InjectMocks
	private RolesInitializer rolesInitializer;


	@Test
	void run_ShouldCreateRolesIfTheyDoNotExist() {
		// Arrange
		String userRoleName = "ROLE_USER";
		String adminRoleName = "ROLE_ADMIN";

		// Mock repository to return empty Optional for both roles
		when(repository.findByName(userRoleName)).thenReturn(Optional.empty());
		when(repository.findByName(adminRoleName)).thenReturn(Optional.empty());

		// Act
		rolesInitializer.run();

		// Assert
		verify(repository, times(1)).findByName(userRoleName);
		verify(repository, times(1)).findByName(adminRoleName);
		verify(repository, times(1)).save(argThat(role -> role.getName().equals(userRoleName)));
		verify(repository, times(1)).save(argThat(role -> role.getName().equals(adminRoleName)));
	}

	@Test
	void run_ShouldNotCreateRolesIfTheyAlreadyExist() {
		// Arrange
		String userRoleName = "ROLE_USER";
		String adminRoleName = "ROLE_ADMIN";

		// Mock repository to return existing roles
		when(repository.findByName(userRoleName)).thenReturn(Optional.of(Role.builder().name(userRoleName).build()));
		when(repository.findByName(adminRoleName)).thenReturn(Optional.of(Role.builder().name(adminRoleName).build()));

		// Act
		rolesInitializer.run();

		// Assert
		verify(repository, times(1)).findByName(userRoleName);
		verify(repository, times(1)).findByName(adminRoleName);
		verify(repository, never()).save(any(Role.class));
	}

	@Test
	void createRoleIfNotExists_ShouldCreateRoleIfItDoesNotExist() {
		// Arrange
		String roleName = "ROLE_TEST";
		when(repository.findByName(roleName)).thenReturn(Optional.empty());

		// Act
		rolesInitializer.createRoleIfNotExists(roleName);

		// Assert
		verify(repository, times(1)).findByName(roleName);
		verify(repository, times(1)).save(argThat(role -> role.getName().equals(roleName)));
	}

	@Test
	void createRoleIfNotExists_ShouldNotCreateRoleIfItAlreadyExists() {
		// Arrange
		String roleName = "ROLE_TEST";
		when(repository.findByName(roleName)).thenReturn(Optional.of(Role.builder().name(roleName).build()));

		// Act
		rolesInitializer.createRoleIfNotExists(roleName);

		// Assert
		verify(repository, times(1)).findByName(roleName);
		verify(repository, never()).save(any(Role.class)); // Role should not be saved
	}
}