package br.com.nichesdev.monevo_wallet.domain.security;

import jakarta.servlet.DispatcherType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.core.annotation.Order;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

@Configuration
public class SecurityConfig {


    @Bean
    @Order(2)
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .dispatcherTypeMatchers(DispatcherType.ERROR).permitAll()
                        .anyRequest().authenticated())
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(Customizer.withDefaults()))
                .build();
    }

    @Bean
    public JwtDecoder jwtDecoder(@Value("${jwt.key}") String key) {
        byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);

        if (keyBytes.length < 32) {
            throw new IllegalArgumentException("JWT_KEY deve possuir pelo menos 32 bytes para HS256");
        }

        SecretKeySpec secretKey = new SecretKeySpec(keyBytes, "HmacSHA256");

        NimbusJwtDecoder decoder = NimbusJwtDecoder
                .withSecretKey(secretKey)
                .macAlgorithm(MacAlgorithm.HS256)
                .build();

        OAuth2TokenValidator<Jwt> identityValidator = jwt -> {
            Object id = jwt.getClaims().get("userId");

            try {
                long userId = Long.parseLong(String.valueOf(id));

                if (userId > 0
                        && jwt.getExpiresAt() != null
                        && jwt.getSubject() != null
                        && !jwt.getSubject().isBlank()) {

                    return OAuth2TokenValidatorResult.success();
                }
            } catch (NumberFormatException ignored) {
                // Claim ausente ou inválida: rejeita o token.
            }
            return OAuth2TokenValidatorResult.failure(
                    new OAuth2Error("invalid_token", "Token sem identificação ou expiração válida", null)
            );
        };
        decoder.setJwtValidator(
                new DelegatingOAuth2TokenValidator<>(
                        JwtValidators.createDefault(),
                        identityValidator
                )
        );
        return decoder;
    }
    @Bean
    public UserDetailsService internalUsers(
            @Value("${internal.trading.password}") String password
    ) {
        if (password.isBlank()) {
            throw new IllegalArgumentException(
                    "TRADING_SERVICE_PASSWORD não pode estar vazio"
            );
        }
        return new InMemoryUserDetailsManager(
                User.withUsername("trading")
                        .password(
                                "{bcrypt}" + new BCryptPasswordEncoder().encode(password)
                        )
                        .roles("TRADING_SERVICE")
                        .build()
        );
    }
    @Bean
    @Order(1)
    public SecurityFilterChain internalSecurityFilterChain(
            HttpSecurity http
    ) throws Exception {
        return http
                .securityMatcher("/internal/**")
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().hasRole("TRADING_SERVICE"))
                .httpBasic(Customizer.withDefaults())
                .build();
    }
}
