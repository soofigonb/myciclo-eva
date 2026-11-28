package cl.myciclo.eva.seguimiento_service.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;

@Service
public class JwtTokenService {

    private final JwtProperties properties;
    private final SecretKey signingKey;

    public JwtTokenService(JwtProperties properties) {
        this.properties = properties;
        this.signingKey = Keys.hmacShaKeyFor(properties.secret().getBytes(StandardCharsets.UTF_8));
    }

    public String generarTokenPrueba(Long usuarioId, String correo) {
        Instant ahora = Instant.now();
        Instant expiracion = ahora.plusSeconds(properties.expirationSeconds());

        return Jwts.builder()
                .subject(String.valueOf(usuarioId))
                .issuer(properties.issuer())
                .audience().add(properties.audience()).and()
                .claim("correo", correo)
                .issuedAt(Date.from(ahora))
                .expiration(Date.from(expiracion))
                .signWith(signingKey)
                .compact();
    }

    public Long obtenerUsuarioIdSiValido(String token) {
        if (token == null || token.isBlank()) {
            return null;
        }
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(signingKey)
                    .requireIssuer(properties.issuer())
                    .requireAudience(properties.audience())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            return Long.parseLong(claims.getSubject());
        } catch (JwtException | NumberFormatException ex) {
            return null;
        }
    }

    public long getExpirationSeconds() {
        return properties.expirationSeconds();
    }
}
