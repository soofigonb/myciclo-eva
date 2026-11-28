package cl.myciclo.eva.seguimiento_service.controller;

import cl.myciclo.eva.seguimiento_service.dto.InicioMenstrualRequest;
import cl.myciclo.eva.seguimiento_service.dto.InicioMenstrualResponse;
import cl.myciclo.eva.seguimiento_service.service.InicioMenstrualService;
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
class InicioMenstrualControllerTest {

    @Mock
    private InicioMenstrualService service;

    @InjectMocks
    private InicioMenstrualController controller;

    private Authentication authentication;
    private final Long usuarioId = 10L;
    private final LocalDate hoy = LocalDate.of(2026, 11, 28);

    @BeforeEach
    void setUp() {
        authentication = new UsernamePasswordAuthenticationToken(usuarioId, null, Collections.emptyList());
    }

    @Test
    void testListar() {
        InicioMenstrualResponse res = new InicioMenstrualResponse(1L, usuarioId, hoy);
        when(service.listarPorUsuario(usuarioId)).thenReturn(List.of(res));

        ResponseEntity<List<InicioMenstrualResponse>> respuesta = controller.listar(authentication);
        assertEquals(HttpStatus.OK, respuesta.getStatusCode());
        assertEquals(1, respuesta.getBody().size());
        assertEquals(1L, respuesta.getBody().get(0).id());
    }

    @Test
    void testObtenerPorId() {
        InicioMenstrualResponse res = new InicioMenstrualResponse(1L, usuarioId, hoy);
        when(service.obtenerPorId(1L, usuarioId)).thenReturn(res);

        ResponseEntity<InicioMenstrualResponse> respuesta = controller.obtenerPorId(1L, authentication);
        assertEquals(HttpStatus.OK, respuesta.getStatusCode());
        assertEquals(1L, respuesta.getBody().id());
    }

    @Test
    void testCrear() {
        InicioMenstrualRequest req = new InicioMenstrualRequest(hoy);
        InicioMenstrualResponse res = new InicioMenstrualResponse(1L, usuarioId, hoy);
        when(service.crear(usuarioId, req)).thenReturn(res);

        ResponseEntity<InicioMenstrualResponse> respuesta = controller.crear(req, authentication);
        assertEquals(HttpStatus.CREATED, respuesta.getStatusCode());
        assertEquals(1L, respuesta.getBody().id());
    }

    @Test
    void testActualizar() {
        InicioMenstrualRequest req = new InicioMenstrualRequest(hoy);
        InicioMenstrualResponse res = new InicioMenstrualResponse(1L, usuarioId, hoy);
        when(service.actualizar(1L, usuarioId, req)).thenReturn(res);

        ResponseEntity<InicioMenstrualResponse> respuesta = controller.actualizar(1L, req, authentication);
        assertEquals(HttpStatus.OK, respuesta.getStatusCode());
        assertEquals(hoy, respuesta.getBody().fecha());
    }

    @Test
    void testEliminar() {
        ResponseEntity<Void> respuesta = controller.eliminar(1L, authentication);
        assertEquals(HttpStatus.NO_CONTENT, respuesta.getStatusCode());
        verify(service).eliminar(1L, usuarioId);
    }
}
