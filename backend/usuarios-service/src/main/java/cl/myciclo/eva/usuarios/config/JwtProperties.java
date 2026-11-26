package cl.myciclo.eva.usuarios.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "eva.jwt")
public record JwtProperties(
        String secret,
        String issuer,
        String audience,
        long expirationSeconds
) {
}
