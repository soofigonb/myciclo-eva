package cl.myciclo.eva.seguimiento_service.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.jwt")
public record JwtProperties(
        String secret,
        String issuer,
        String audience,
        long expirationSeconds
) {
}
