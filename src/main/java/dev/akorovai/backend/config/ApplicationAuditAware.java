package dev.akorovai.backend.config;

import dev.akorovai.backend.user.User;
import edu.umd.cs.findbugs.annotations.NonNull;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component("auditorAware")
public class ApplicationAuditAware implements AuditorAware<Long> {
	@NonNull
	@Override
	public Optional<Long> getCurrentAuditor() {
		return Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
				       .filter(this::isValidAuthentication)
				       .map(Authentication::getPrincipal)
				       .filter(User.class::isInstance)
				       .map(User.class::cast)
				       .map(User::getId);
	}

	private boolean isValidAuthentication(Authentication authentication) {
		return authentication.isAuthenticated() && !(authentication instanceof AnonymousAuthenticationToken);
	}
}