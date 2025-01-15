package dev.akorovai.backend.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetails;


import java.io.IOException;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThat;


@ExtendWith(MockitoExtension.class)
public class JwtFilterTest {

	@Mock
	private JwtService jwtService;

	@Mock
	private UserDetailsService userDetailsService;

	@Mock
	private HttpServletRequest request;

	@Mock
	private HttpServletResponse response;

	@Mock
	private FilterChain filterChain;

	@InjectMocks
	private JwtFilter jwtFilter;

	@BeforeEach
	void setUp() {
		SecurityContextHolder.clearContext();
	}

	@Test
	void doFilterInternal_ShouldSkipAuthenticationPath() throws ServletException, IOException {
		// Arrange
		when(request.getServletPath()).thenReturn("/api/auth/login");

		// Act
		jwtFilter.doFilterInternal(request, response, filterChain);

		// Assert
		verify(filterChain).doFilter(request, response);
		verifyNoInteractions(jwtService, userDetailsService);
	}

	@Test
	void doFilterInternal_ShouldSkipWhenNoJwtToken() throws ServletException, IOException {
		// Arrange
		when(request.getServletPath()).thenReturn("/api/user");
		when(request.getHeader("Authorization")).thenReturn(null);

		// Act
		jwtFilter.doFilterInternal(request, response, filterChain);

		// Assert
		verify(filterChain).doFilter(request, response);
		verifyNoInteractions(jwtService, userDetailsService);
	}

	@Test
	void doFilterInternal_ShouldAuthenticateUserWithValidJwt() throws ServletException, IOException {
		// Arrange
		String jwt = "valid.jwt.token";
		String userEmail = "test@example.com";
		UserDetails userDetails = mock(UserDetails.class);

		when(request.getServletPath()).thenReturn("/api/user");
		when(request.getHeader("Authorization")).thenReturn("Bearer " + jwt);
		when(jwtService.extractUsername(jwt)).thenReturn(userEmail);
		when(userDetailsService.loadUserByUsername(userEmail)).thenReturn(userDetails);
		when(jwtService.isTokenValid(jwt, userDetails)).thenReturn(true);

		// Act
		jwtFilter.doFilterInternal(request, response, filterChain);

		// Assert
		verify(jwtService).extractUsername(jwt);
		verify(userDetailsService).loadUserByUsername(userEmail);
		verify(jwtService).isTokenValid(jwt, userDetails);

		UsernamePasswordAuthenticationToken authentication =
				(UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
		assertThat(authentication).isNotNull();
		assertThat(authentication.getPrincipal()).isEqualTo(userDetails);
		assertThat(authentication.getDetails()).isInstanceOf(WebAuthenticationDetails.class); // Correct assertion

		verify(filterChain).doFilter(request, response);
	}

	@Test
	void doFilterInternal_ShouldNotAuthenticateUserWithInvalidJwt() throws ServletException, IOException {
		// Arrange
		String jwt = "invalid.jwt.token";
		String userEmail = "test@example.com";
		UserDetails userDetails = mock(UserDetails.class);

		when(request.getServletPath()).thenReturn("/api/user");
		when(request.getHeader("Authorization")).thenReturn("Bearer " + jwt);
		when(jwtService.extractUsername(jwt)).thenReturn(userEmail);
		when(userDetailsService.loadUserByUsername(userEmail)).thenReturn(userDetails);
		when(jwtService.isTokenValid(jwt, userDetails)).thenReturn(false);

		// Act
		jwtFilter.doFilterInternal(request, response, filterChain);

		// Assert
		verify(jwtService).extractUsername(jwt);
		verify(userDetailsService).loadUserByUsername(userEmail);
		verify(jwtService).isTokenValid(jwt, userDetails);

		assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
		verify(filterChain).doFilter(request, response);
	}

	@Test
	void doFilterInternal_ShouldSkipWhenUserAlreadyAuthenticated() throws ServletException, IOException {
		// Arrange
		String jwt = "valid.jwt.token";
		String userEmail = "test@example.com";
		UserDetails userDetails = mock(UserDetails.class);

		// Simulate an already authenticated user
		UsernamePasswordAuthenticationToken existingAuth = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
		SecurityContextHolder.getContext().setAuthentication(existingAuth);

		when(request.getServletPath()).thenReturn("/api/user");
		when(request.getHeader("Authorization")).thenReturn("Bearer " + jwt);
		when(jwtService.extractUsername(jwt)).thenReturn(userEmail);

		// Act
		jwtFilter.doFilterInternal(request, response, filterChain);

		// Assert
		verify(jwtService).extractUsername(jwt);
		verifyNoMoreInteractions(userDetailsService, jwtService); // No further authentication should happen
		verify(filterChain).doFilter(request, response);
	}

	@Test
	void isAuthenticationPath_ShouldReturnTrueForAuthPath() {
		// Arrange
		when(request.getServletPath()).thenReturn("/api/auth/login");

		// Act
		boolean isAuthPath = jwtFilter.isAuthenticationPath(request);

		// Assert
		assertThat(isAuthPath).isTrue();
	}

	@Test
	void isAuthenticationPath_ShouldReturnFalseForNonAuthPath() {
		// Arrange
		when(request.getServletPath()).thenReturn("/api/user");

		// Act
		boolean isAuthPath = jwtFilter.isAuthenticationPath(request);

		// Assert
		assertThat(isAuthPath).isFalse();
	}

	@Test
	void getJwtFromRequest_ShouldReturnTokenFromHeader() {
		// Arrange
		when(request.getHeader("Authorization")).thenReturn("Bearer valid.jwt.token");

		// Act
		String jwt = jwtFilter.getJwtFromRequest(request);

		// Assert
		assertThat(jwt).isEqualTo("valid.jwt.token");
	}

	@Test
	void getJwtFromRequest_ShouldReturnNullForInvalidHeader() {
		// Arrange
		when(request.getHeader("Authorization")).thenReturn("InvalidHeader");

		// Act
		String jwt = jwtFilter.getJwtFromRequest(request);

		// Assert
		assertThat(jwt).isNull();
	}
}