package cl.myciclo.eva.seguimiento_service.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class InicioMenstrualTest {

    @Test
    void testGettersSettersAndConstructors() {
        LocalDate hoy = LocalDate.of(2026, 11, 28);
        InicioMenstrual inicio = new InicioMenstrual(1L, 10L, hoy);

        assertEquals(1L, inicio.getId());
        assertEquals(10L, inicio.getUsuarioId());
        assertEquals(hoy, inicio.getFecha());

        inicio.setId(2L);
        inicio.setUsuarioId(20L);
        LocalDate manana = hoy.plusDays(1);
        inicio.setFecha(manana);

        assertEquals(2L, inicio.getId());
        assertEquals(20L, inicio.getUsuarioId());
        assertEquals(manana, inicio.getFecha());
    }

    @Test
    void testEqualsAndHashCode() {
        LocalDate hoy = LocalDate.of(2026, 11, 28);
        InicioMenstrual inicio1 = new InicioMenstrual(1L, 10L, hoy);
        InicioMenstrual inicio2 = new InicioMenstrual(1L, 10L, hoy);
        InicioMenstrual inicio3 = new InicioMenstrual(2L, 10L, hoy);

        assertEquals(inicio1, inicio2);
        assertEquals(inicio1.hashCode(), inicio2.hashCode());
        assertNotEquals(inicio1, inicio3);
        assertNotEquals(inicio1, null);
        assertNotEquals(inicio1, "un string");
    }
}
