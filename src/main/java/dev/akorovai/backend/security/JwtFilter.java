package dev.akorovai.backend.security;


import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Service;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {
	private final JwtService jwtService;
	private final UserDetailsService userDetailsService;

	@Override
	public void doFilterInternal( @NonNull HttpServletRequest request,
	                         @NonNull HttpServletResponse response, @NonNull FilterChain filterChain ) throws ServletException, IOException {
		if ( isAuthenticationPath(request) ) {
			filterChain.doFilter(request, response);
			return;
		}

		String jwt = getJwtFromRequest(request);
		if ( jwt == null ) {
			filterChain.doFilter(request, response);
			return;
		}

		String userEmail = jwtService.extractUsername(jwt);
		if ( userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null ) {
			authenticateUser(userEmail, jwt, request);
		}

		filterChain.doFilter(request, response);
	}

	public boolean isAuthenticationPath( HttpServletRequest request ) {
		String requestURI = request.getServletPath();
		return requestURI.contains("/api/auth") && !requestURI.contains("logout");
	}

	protected String getJwtFromRequest( HttpServletRequest request ) {
		final String authHeader = request.getHeader("Authorization");
		if ( authHeader != null && authHeader.startsWith("Bearer ") ) {
			return authHeader.substring(7);
		}
		return null;
	}

	private void authenticateUser( String userEmail, String jwt, HttpServletRequest request ) {
		UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);
		if ( jwtService.isTokenValid(jwt, userDetails) ) {
			UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
			authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
			SecurityContextHolder.getContext().setAuthentication(authToken);
		}
	}
}
