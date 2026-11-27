package cl.myciclo.eva.usuarios.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class JwtTokenServiceTest {

    private JwtTokenService jwtTokenService;

    @BeforeEach
    void setUp() {
        JwtProperties properties = new JwtProperties(
                "MiClaveSecretaDeAlMenos32BytesParaHS256DePruebas!",
                "myciclo-eva-usuarios",
                "myciclo-eva-api",
                86400L
        );
        jwtTokenService = new JwtTokenService(properties);
    }

    @Test
    void generarYValidarToken_exitoso() {
        String token = jwtTokenService.generarToken(123L, "nitsi@ejemplo.cl");
        assertNotNull(token);

        Long usuarioId = jwtTokenService.obtenerUsuarioIdSiValido(token);
        assertEquals(123L, usuarioId);
        assertEquals(86400L, jwtTokenService.getExpirationSeconds());
    }

    @Test
    void validarToken_invalido_retornaNull() {
        Long usuarioId = jwtTokenService.obtenerUsuarioIdSiValido("token_totalmente_invalido");
        assertNull(usuarioId);
    }
}
