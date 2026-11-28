package cl.myciclo.eva.seguimiento_service.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JwtTokenServiceTest {

    private JwtTokenService jwtTokenService;

    @BeforeEach
    void setUp() {
        JwtProperties props = new JwtProperties(
                "MiClaveSuperSecretaDeAlMenos32CaracteresMyCiclo2026!",
                "myciclo-eva-usuarios",
                "myciclo-eva-api",
                3600
        );
        jwtTokenService = new JwtTokenService(props);
    }

    @Test
    void testGenerarYValidarTokenExitoso() {
        String token = jwtTokenService.generarTokenPrueba(42L, "jona@duocuc.cl");
        assertNotNull(token);

        Long usuarioId = jwtTokenService.obtenerUsuarioIdSiValido(token);
        assertEquals(42L, usuarioId);
        assertEquals(3600, jwtTokenService.getExpirationSeconds());
    }

    @Test
    void testTokenInvalidoOAlteradoRetornaNull() {
        assertNull(jwtTokenService.obtenerUsuarioIdSiValido("token_invalido_totalmente"));
        assertNull(jwtTokenService.obtenerUsuarioIdSiValido(null));

        String token = jwtTokenService.generarTokenPrueba(42L, "jona@duocuc.cl");
        String tokenCorrupto = token + "xyz";
        assertNull(jwtTokenService.obtenerUsuarioIdSiValido(tokenCorrupto));
    }
}
