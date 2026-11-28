package cl.myciclo.eva.seguimiento_service.controller;

import cl.myciclo.eva.seguimiento_service.dto.BitacoraRequest;
import cl.myciclo.eva.seguimiento_service.dto.BitacoraResponse;
import cl.myciclo.eva.seguimiento_service.service.BitacoraService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BitacoraControllerTest {

    @Mock
    private BitacoraService service;

    @InjectMocks
    private BitacoraController controller;

    private Authentication authentication;
    private final Long usuarioId = 10L;
    private final LocalDate hoy = LocalDate.of(2026, 11, 28);

    @BeforeEach
    void setUp() {
        authentication = new UsernamePasswordAuthenticationToken(usuarioId, null, Collections.emptyList());
    }

    @Test
    void testListar() {
        BitacoraResponse res = new BitacoraResponse(
                1L, usuarioId, hoy, "regular", "cólico", "bien", "medio", "normal", "nota"
        );
        when(service.listarPorUsuario(usuarioId)).thenReturn(List.of(res));

        ResponseEntity<List<BitacoraResponse>> respuesta = controller.listar(authentication);
        assertEquals(HttpStatus.OK, respuesta.getStatusCode());
        assertEquals(1, respuesta.getBody().size());
        assertEquals(1L, respuesta.getBody().get(0).id());
    }

    @Test
    void testObtenerPorId() {
        BitacoraResponse res = new BitacoraResponse(
                1L, usuarioId, hoy, "regular", "cólico", "bien", "medio", "normal", "nota"
        );
        when(service.obtenerPorId(1L, usuarioId)).thenReturn(res);

        ResponseEntity<BitacoraResponse> respuesta = controller.obtenerPorId(1L, authentication);
        assertEquals(HttpStatus.OK, respuesta.getStatusCode());
        assertEquals(1L, respuesta.getBody().id());
        assertEquals("bien", respuesta.getBody().estadoAnimo());
    }

    @Test
    void testCrear() {
        BitacoraRequest req = new BitacoraRequest(
                hoy, "regular", "cólico", "bien", "medio", "normal", "nota"
        );
        BitacoraResponse res = new BitacoraResponse(
                1L, usuarioId, hoy, "regular", "cólico", "bien", "medio", "normal", "nota"
        );
        when(service.crear(usuarioId, req)).thenReturn(res);

        ResponseEntity<BitacoraResponse> respuesta = controller.crear(req, authentication);
        assertEquals(HttpStatus.CREATED, respuesta.getStatusCode());
        assertEquals(1L, respuesta.getBody().id());
    }

    @Test
    void testActualizar() {
        BitacoraRequest req = new BitacoraRequest(
                hoy, "regular", "cólico", "bien", "medio", "normal", "nota"
        );
        BitacoraResponse res = new BitacoraResponse(
                1L, usuarioId, hoy, "regular", "cólico", "bien", "medio", "normal", "nota"
        );
        when(service.actualizar(1L, usuarioId, req)).thenReturn(res);

        ResponseEntity<BitacoraResponse> respuesta = controller.actualizar(1L, req, authentication);
        assertEquals(HttpStatus.OK, respuesta.getStatusCode());
        assertEquals("regular", respuesta.getBody().patron());
    }

    @Test
    void testEliminar() {
        ResponseEntity<Void> respuesta = controller.eliminar(1L, authentication);
        assertEquals(HttpStatus.NO_CONTENT, respuesta.getStatusCode());
        verify(service).eliminar(1L, usuarioId);
    }
}
