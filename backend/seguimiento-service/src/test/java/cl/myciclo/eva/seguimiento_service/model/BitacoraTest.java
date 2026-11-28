package cl.myciclo.eva.seguimiento_service.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class BitacoraTest {

    @Test
    void testGettersSettersAndConstructors() {
        LocalDate hoy = LocalDate.of(2026, 11, 28);
        Bitacora bitacora = new Bitacora(
                1L, 10L, hoy, "regular", "cólicos leves", "tranquila", "moderado", "normal", "todo bien"
        );

        assertEquals(1L, bitacora.getId());
        assertEquals(10L, bitacora.getUsuarioId());
        assertEquals(hoy, bitacora.getFecha());
        assertEquals("regular", bitacora.getPatron());
        assertEquals("cólicos leves", bitacora.getSintomas());
        assertEquals("tranquila", bitacora.getEstadoAnimo());
        assertEquals("moderado", bitacora.getFlujoMenstrual());
        assertEquals("normal", bitacora.getFlujoVaginal());
        assertEquals("todo bien", bitacora.getNotas());

        bitacora.setId(2L);
        bitacora.setUsuarioId(20L);
        bitacora.setFecha(hoy.plusDays(1));
        bitacora.setPatron("irregular");
        bitacora.setSintomas("dolor");
        bitacora.setEstadoAnimo("cansada");
        bitacora.setFlujoMenstrual("abundante");
        bitacora.setFlujoVaginal("acuoso");
        bitacora.setNotas("descanso");

        assertEquals(2L, bitacora.getId());
        assertEquals(20L, bitacora.getUsuarioId());
        assertEquals(hoy.plusDays(1), bitacora.getFecha());
        assertEquals("irregular", bitacora.getPatron());
        assertEquals("dolor", bitacora.getSintomas());
        assertEquals("cansada", bitacora.getEstadoAnimo());
        assertEquals("abundante", bitacora.getFlujoMenstrual());
        assertEquals("acuoso", bitacora.getFlujoVaginal());
        assertEquals("descanso", bitacora.getNotas());
    }

    @Test
    void testEqualsAndHashCode() {
        LocalDate hoy = LocalDate.of(2026, 11, 28);
        Bitacora b1 = new Bitacora(1L, 10L, hoy, "p", "s", "a", "fm", "fv", "n");
        Bitacora b2 = new Bitacora(1L, 10L, hoy, "p", "s", "a", "fm", "fv", "n");
        Bitacora b3 = new Bitacora(2L, 10L, hoy, "p", "s", "a", "fm", "fv", "n");

        assertEquals(b1, b2);
        assertEquals(b1.hashCode(), b2.hashCode());
        assertNotEquals(b1, b3);
        assertNotEquals(b1, null);
        assertNotEquals(b1, new Object());
    }
}
