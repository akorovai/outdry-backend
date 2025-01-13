package dev.akorovai.backend.auth;

import dev.akorovai.backend.auth.dto.AuthenticationRequest;
import dev.akorovai.backend.auth.dto.AuthenticationResponse;
import dev.akorovai.backend.auth.dto.RecoveryRequest;
import dev.akorovai.backend.auth.dto.RegistrationRequest;
import dev.akorovai.backend.handler.refresh_token.TokenRefreshException;
import dev.akorovai.backend.refresh_token.RefreshToken;
import dev.akorovai.backend.refresh_token.RefreshTokenService;
import dev.akorovai.backend.refresh_token.TokenRefreshRequest;
import dev.akorovai.backend.security.JwtService;
import dev.akorovai.backend.security.ResponseRecord;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthenticationController {

	private final AuthenticationService service;
	private final RefreshTokenService refreshTokenService;
	private final JwtService jwtService;

	@PostMapping("/register")
	public ResponseEntity<ResponseRecord> register(@RequestBody @Valid RegistrationRequest request) {
		String message = service.register(request);
		return buildResponseEntity(HttpStatus.ACCEPTED, message);
	}

	@PostMapping("/authenticate")
	public ResponseEntity<AuthenticationResponse> authenticate(@Valid @RequestBody AuthenticationRequest request) {
		return ResponseEntity.ok(service.authenticate(request));
	}

	@PostMapping("/logout")
	public ResponseEntity<ResponseRecord> logout() {
		String response = service.logout();
		return buildResponseEntity(HttpStatus.OK, response);
	}

	@PostMapping("/activate-account")
	public ResponseEntity<ResponseRecord> activateAccount(@RequestParam String emailToken) {
		String jwtToken = service.activateAccount(emailToken);
		return buildResponseEntity(HttpStatus.ACCEPTED, jwtToken);
	}

	@GetMapping("/recover-account")
	public ResponseEntity<ResponseRecord> recoverByEmail(@RequestParam String email) {
		String responseMessage = service.recoverAccount(email);
		return buildResponseEntity(HttpStatus.ACCEPTED, responseMessage);
	}

	@PostMapping("/change-password")
	public ResponseEntity<ResponseRecord> changePassword(@RequestHeader("Authorization") String authorizationHeader, @RequestBody @Valid RecoveryRequest request) {
		String token = authorizationHeader.replace("Bearer ", "").trim();
		ResponseRecord responseRecord = service.changeUserPassword(token, request.getPassword());
		return ResponseEntity.ok(responseRecord);
	}

	@PostMapping("/refresh-token")
	public ResponseEntity<ResponseRecord> refreshToken(@RequestBody TokenRefreshRequest request) {
		return refreshTokenService.findByToken(request.getRefreshToken())
				       .map(refreshTokenService::verifyExpiration)
				       .map(RefreshToken::getUser)
				       .map(user -> {
					       Map<String, Object> claims = Map.of("username", user.getNickname());
					       String newAccessToken = jwtService.generateToken(claims, user);
					       return buildResponseEntity(HttpStatus.OK, newAccessToken);
				       })
				       .orElseThrow(() -> new TokenRefreshException("Refresh token is invalid!"));
	}

	private ResponseEntity<ResponseRecord> buildResponseEntity(HttpStatus status, String message) {
		return ResponseEntity.status(status)
				       .body(ResponseRecord.builder()
						             .code(status.value())
						             .message(message)
						             .build());
	}
}