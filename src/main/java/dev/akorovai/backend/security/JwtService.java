package dev.akorovai.backend.security;

import dev.akorovai.backend.handler.user.UserNotFoundException;
import dev.akorovai.backend.user.User;
import dev.akorovai.backend.user.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class JwtService {
	private final UserRepository userRepository;

	@Value("${application.security.jwt.secret-key}")
	private String secretKey;

	@Value("${application.security.jwt.access-token.expiration}")
	private long accessTokenExpiration;

	@Value("${application.security.jwt.refresh-token.expiration}")
	private long refreshTokenExpiration;

	public String extractUsername( String token ) {
		return extractClaim(token, Claims::getSubject);
	}

	public <T> T extractClaim( String token, Function<Claims, T> claimsResolver ) {
		final Claims claims = extractAllClaims(token);
		return claimsResolver.apply(claims);
	}


	public String generateToken( Map<String, Object> extraClaims, UserDetails userDetails ) {
		return buildToken(extraClaims, userDetails, accessTokenExpiration);
	}

	public String generateRefreshToken( Map<String, Object> extraClaims, UserDetails userDetails ) {
		return buildToken(extraClaims, userDetails, refreshTokenExpiration);
	}

	String buildToken( Map<String, Object> extraClaims, UserDetails userDetails, long expiration ) {
		var authorities = userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();
		return Jwts.builder().setClaims(extraClaims).setSubject(userDetails.getUsername()).setIssuedAt(new Date(System.currentTimeMillis())).setExpiration(new Date(System.currentTimeMillis() + expiration)).claim("authorities", authorities).signWith(getSignInKey()).compact();
	}


	public boolean isTokenValid( String token, UserDetails userDetails ) {
		final String username = extractUsername(token);
		return ( username.equals(userDetails.getUsername()) ) && !isTokenExpired(token);
	}


	boolean isTokenExpired( String token ) {
		return extractExpiration(token).before(new Date());
	}


	Date extractExpiration( String token ) {
		return extractClaim(token, Claims::getExpiration);
	}


	Claims extractAllClaims( String token ) {
		return Jwts.parserBuilder().setSigningKey(getSignInKey()).build().parseClaimsJws(token).getBody();
	}


	Key getSignInKey() {
		byte[] keyBytes = Decoders.BASE64.decode(secretKey);
		return Keys.hmacShaKeyFor(keyBytes);
	}

	public User getAuthenticatedUser() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		return userRepository.findByEmail(authentication.getName())
				       .orElseThrow(() -> new UserNotFoundException("User not found"));
	}

}
