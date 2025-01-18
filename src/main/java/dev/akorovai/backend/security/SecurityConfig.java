package dev.akorovai.backend.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Map;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity(securedEnabled = true)
public class SecurityConfig {


    private final AuthenticationProvider authenticationProvider;
    private final JwtFilter jwtFilter;


    private static final Map<String, Map<HttpMethod, String[]>> ROLE_PATHS = Map.of(
            "PUBLIC", Map.of(
                    HttpMethod.POST, new String[]{"/api/auth/*"}
            ),
            "ADMIN", Map.of(
                    HttpMethod.POST, new String[]{"/api/products", "/api/products/{productId}/discount"},
                    HttpMethod.GET, new String[]{"/api/products/filter"},
                    HttpMethod.PUT, new String[]{"/api/products/{productId}"},
                    HttpMethod.DELETE, new String[]{"/api/products/{productId}"}
            )
    );

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(request -> {
                    configurePublicPaths(request);
                    configureRoleBasedPaths(request);
                    request.anyRequest().authenticated();
                })
                .sessionManagement(session -> session.sessionCreationPolicy(STATELESS))
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    private void configurePublicPaths(
            AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry request
    ) {
        ROLE_PATHS.get("PUBLIC").forEach((method, paths) -> {
            request.requestMatchers(method, paths).permitAll();
        });
    }

    private void configureRoleBasedPaths(
            AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry request
    ) {
        ROLE_PATHS.forEach((role, methodMap) -> {
            if (!role.equals("PUBLIC")) {
                methodMap.forEach((method, paths) -> {
                    request.requestMatchers(method, paths).hasRole(role);
                });
            }
        });
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedOrigin("http://localhost:5173");
        configuration.addAllowedMethod("*");
        configuration.addAllowedHeader("*");
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}