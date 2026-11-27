package cl.myciclo.eva.usuarios.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UsuarioTest {

    @Test
    void testUsuarioEntity() {
        Usuario usuario = new Usuario("Nitsi", "nitsi@test.cl", "hash", LocalDate.of(1998, 5, 14));
        usuario.setId(10L);
        usuario.alCrear();
        usuario.alActualizar();

        assertEquals(10L, usuario.getId());
        assertEquals("Nitsi", usuario.getNombre());
        assertEquals("nitsi@test.cl", usuario.getCorreo());
        assertEquals("hash", usuario.getContrasenaHash());
        assertEquals(LocalDate.of(1998, 5, 14), usuario.getFechaNacimiento());
        assertNotNull(usuario.getCreadoEn());
        assertNotNull(usuario.getActualizadoEn());
        assertTrue(usuario.toString().contains("nitsi@test.cl"));
    }
}
