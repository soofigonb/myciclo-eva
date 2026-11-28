package cl.myciclo.eva.seguimiento_service.dto;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class DtoTest {

    @Test
    void testInicioMenstrualDtos() {
        LocalDate hoy = LocalDate.of(2026, 11, 28);
        InicioMenstrualRequest req = new InicioMenstrualRequest(hoy);
        assertEquals(hoy, req.fecha());

        InicioMenstrualResponse res = new InicioMenstrualResponse(1L, 5L, hoy);
        assertEquals(1L, res.id());
        assertEquals(5L, res.usuarioId());
        assertEquals(hoy, res.fecha());
    }

    @Test
    void testBitacoraDtos() {
        LocalDate hoy = LocalDate.of(2026, 11, 28);
        BitacoraRequest req = new BitacoraRequest(
                hoy, "regular", "cólico", "bien", "moderado", "normal", "nota"
        );
        assertEquals(hoy, req.fecha());
        assertEquals("regular", req.patron());
        assertEquals("cólico", req.sintomas());
        assertEquals("bien", req.estadoAnimo());
        assertEquals("moderado", req.flujoMenstrual());
        assertEquals("normal", req.flujoVaginal());
        assertEquals("nota", req.notas());

        BitacoraResponse res = new BitacoraResponse(
                10L, 5L, hoy, "regular", "cólico", "bien", "moderado", "normal", "nota"
        );
        assertEquals(10L, res.id());
        assertEquals(5L, res.usuarioId());
        assertEquals(hoy, res.fecha());
        assertEquals("regular", res.patron());
    }

    @Test
    void testErrorResponse() {
        ErrorResponse err = new ErrorResponse("CODIGO", "Mensaje", Map.of("campo", "error"));
        assertEquals("CODIGO", err.codigo());
        assertEquals("Mensaje", err.mensaje());
        assertEquals("error", err.campos().get("campo"));
    }
}
