package cl.myciclo.eva.seguimiento_service.service;

import cl.myciclo.eva.seguimiento_service.dto.BitacoraRequest;
import cl.myciclo.eva.seguimiento_service.dto.BitacoraResponse;
import cl.myciclo.eva.seguimiento_service.exception.RegistroNoEncontradoException;
import cl.myciclo.eva.seguimiento_service.model.Bitacora;
import cl.myciclo.eva.seguimiento_service.repository.BitacoraRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BitacoraServiceTest {

    @Mock
    private BitacoraRepository repository;

    private BitacoraService service;

    private final Long usuarioId = 10L;
    private final LocalDate hoy = LocalDate.of(2026, 11, 28);

    @BeforeEach
    void setUp() {
        service = new BitacoraService(repository);
    }

    @Test
    void testListarPorUsuario() {
        Bitacora entidad = new Bitacora(
                1L, usuarioId, hoy, "regular", "cólico", "feliz", "medio", "normal", "nota"
        );
        when(repository.findByUsuarioIdOrderByFechaDesc(usuarioId)).thenReturn(List.of(entidad));

        List<BitacoraResponse> lista = service.listarPorUsuario(usuarioId);
        assertEquals(1, lista.size());
        assertEquals(1L, lista.get(0).id());
        assertEquals("regular", lista.get(0).patron());
    }

    @Test
    void testObtenerPorIdExitoso() {
        Bitacora entidad = new Bitacora(
                1L, usuarioId, hoy, "regular", "cólico", "feliz", "medio", "normal", "nota"
        );
        when(repository.findByIdAndUsuarioId(1L, usuarioId)).thenReturn(Optional.of(entidad));

        BitacoraResponse res = service.obtenerPorId(1L, usuarioId);
        assertNotNull(res);
        assertEquals(1L, res.id());
        assertEquals("feliz", res.estadoAnimo());
    }

    @Test
    void testObtenerPorIdLanza404SiNoExisteOOtroUsuario() {
        when(repository.findByIdAndUsuarioId(99L, usuarioId)).thenReturn(Optional.empty());

        assertThrows(RegistroNoEncontradoException.class, () -> service.obtenerPorId(99L, usuarioId));
    }

    @Test
    void testCrearExitoso() {
        BitacoraRequest req = new BitacoraRequest(
                hoy, "regular", "ninguno", "tranquila", "ligero", "normal", "bien"
        );
        when(repository.existsByUsuarioIdAndFecha(usuarioId, hoy)).thenReturn(false);
        when(repository.save(any(Bitacora.class))).thenAnswer(invocation -> {
            Bitacora guardado = invocation.getArgument(0);
            guardado.setId(1L);
            return guardado;
        });

        BitacoraResponse res = service.crear(usuarioId, req);
        assertNotNull(res);
        assertEquals(1L, res.id());
        assertEquals("tranquila", res.estadoAnimo());
    }

    @Test
    void testCrearDuplicadoLanzaExcepcion() {
        BitacoraRequest req = new BitacoraRequest(
                hoy, "regular", "ninguno", "tranquila", "ligero", "normal", "bien"
        );
        when(repository.existsByUsuarioIdAndFecha(usuarioId, hoy)).thenReturn(true);

        assertThrows(DataIntegrityViolationException.class, () -> service.crear(usuarioId, req));
        verify(repository, never()).save(any());
    }

    @Test
    void testActualizarExitoso() {
        Bitacora entidad = new Bitacora(
                1L, usuarioId, hoy, "regular", "ninguno", "tranquila", "ligero", "normal", "bien"
        );
        LocalDate manana = hoy.plusDays(1);
        BitacoraRequest req = new BitacoraRequest(
                manana, "irregular", "dolor de cabeza", "cansada", "moderado", "acuoso", "dormir más"
        );

        when(repository.findByIdAndUsuarioId(1L, usuarioId)).thenReturn(Optional.of(entidad));
        when(repository.existsByUsuarioIdAndFecha(usuarioId, manana)).thenReturn(false);
        when(repository.save(any(Bitacora.class))).thenReturn(entidad);

        BitacoraResponse res = service.actualizar(1L, usuarioId, req);
        assertEquals(manana, res.fecha());
        assertEquals("irregular", res.patron());
        assertEquals("cansada", res.estadoAnimo());
    }

    @Test
    void testActualizarNoEncontradoLanzaExcepcion() {
        BitacoraRequest req = new BitacoraRequest(
                hoy, "regular", "ninguno", "tranquila", "ligero", "normal", "bien"
        );
        when(repository.findByIdAndUsuarioId(99L, usuarioId)).thenReturn(Optional.empty());

        assertThrows(RegistroNoEncontradoException.class, () -> service.actualizar(99L, usuarioId, req));
    }

    @Test
    void testActualizarFechaDuplicadaLanzaExcepcion() {
        Bitacora entidad = new Bitacora(
                1L, usuarioId, hoy, "regular", "ninguno", "tranquila", "ligero", "normal", "bien"
        );
        LocalDate manana = hoy.plusDays(1);
        BitacoraRequest req = new BitacoraRequest(
                manana, "irregular", "dolor", "cansada", "moderado", "acuoso", "notas"
        );

        when(repository.findByIdAndUsuarioId(1L, usuarioId)).thenReturn(Optional.of(entidad));
        when(repository.existsByUsuarioIdAndFecha(usuarioId, manana)).thenReturn(true);

        assertThrows(DataIntegrityViolationException.class, () -> service.actualizar(1L, usuarioId, req));
    }

    @Test
    void testEliminarExitoso() {
        Bitacora entidad = new Bitacora(
                1L, usuarioId, hoy, "regular", "ninguno", "tranquila", "ligero", "normal", "bien"
        );
        when(repository.findByIdAndUsuarioId(1L, usuarioId)).thenReturn(Optional.of(entidad));

        service.eliminar(1L, usuarioId);
        verify(repository).delete(entidad);
    }

    @Test
    void testEliminarNoEncontradoLanzaExcepcion() {
        when(repository.findByIdAndUsuarioId(99L, usuarioId)).thenReturn(Optional.empty());

        assertThrows(RegistroNoEncontradoException.class, () -> service.eliminar(99L, usuarioId));
        verify(repository, never()).delete(any());
    }
}
