package cl.myciclo.eva.seguimiento_service.service;

import cl.myciclo.eva.seguimiento_service.dto.InicioMenstrualRequest;
import cl.myciclo.eva.seguimiento_service.dto.InicioMenstrualResponse;
import cl.myciclo.eva.seguimiento_service.exception.RegistroNoEncontradoException;
import cl.myciclo.eva.seguimiento_service.model.InicioMenstrual;
import cl.myciclo.eva.seguimiento_service.repository.InicioMenstrualRepository;
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
class InicioMenstrualServiceTest {

    @Mock
    private InicioMenstrualRepository repository;

    private InicioMenstrualService service;

    private final Long usuarioId = 10L;
    private final LocalDate hoy = LocalDate.of(2026, 11, 28);

    @BeforeEach
    void setUp() {
        service = new InicioMenstrualService(repository);
    }

    @Test
    void testListarPorUsuario() {
        InicioMenstrual entidad = new InicioMenstrual(1L, usuarioId, hoy);
        when(repository.findByUsuarioIdOrderByFechaDesc(usuarioId)).thenReturn(List.of(entidad));

        List<InicioMenstrualResponse> lista = service.listarPorUsuario(usuarioId);
        assertEquals(1, lista.size());
        assertEquals(1L, lista.get(0).id());
        assertEquals(hoy, lista.get(0).fecha());
    }

    @Test
    void testObtenerPorIdExitoso() {
        InicioMenstrual entidad = new InicioMenstrual(1L, usuarioId, hoy);
        when(repository.findByIdAndUsuarioId(1L, usuarioId)).thenReturn(Optional.of(entidad));

        InicioMenstrualResponse res = service.obtenerPorId(1L, usuarioId);
        assertNotNull(res);
        assertEquals(1L, res.id());
        assertEquals(usuarioId, res.usuarioId());
    }

    @Test
    void testObtenerPorIdLanza404SiNoExisteOOtroUsuario() {
        when(repository.findByIdAndUsuarioId(99L, usuarioId)).thenReturn(Optional.empty());

        assertThrows(RegistroNoEncontradoException.class, () -> service.obtenerPorId(99L, usuarioId));
    }

    @Test
    void testCrearExitoso() {
        InicioMenstrualRequest req = new InicioMenstrualRequest(hoy);
        when(repository.existsByUsuarioIdAndFecha(usuarioId, hoy)).thenReturn(false);
        when(repository.save(any(InicioMenstrual.class))).thenAnswer(invocation -> {
            InicioMenstrual guardado = invocation.getArgument(0);
            guardado.setId(1L);
            return guardado;
        });

        InicioMenstrualResponse res = service.crear(usuarioId, req);
        assertNotNull(res);
        assertEquals(1L, res.id());
        assertEquals(hoy, res.fecha());
    }

    @Test
    void testCrearDuplicadoLanzaExcepcion() {
        InicioMenstrualRequest req = new InicioMenstrualRequest(hoy);
        when(repository.existsByUsuarioIdAndFecha(usuarioId, hoy)).thenReturn(true);

        assertThrows(DataIntegrityViolationException.class, () -> service.crear(usuarioId, req));
        verify(repository, never()).save(any());
    }

    @Test
    void testActualizarExitoso() {
        InicioMenstrual entidad = new InicioMenstrual(1L, usuarioId, hoy);
        LocalDate manana = hoy.plusDays(1);
        InicioMenstrualRequest req = new InicioMenstrualRequest(manana);

        when(repository.findByIdAndUsuarioId(1L, usuarioId)).thenReturn(Optional.of(entidad));
        when(repository.existsByUsuarioIdAndFecha(usuarioId, manana)).thenReturn(false);
        when(repository.save(any(InicioMenstrual.class))).thenReturn(entidad);

        InicioMenstrualResponse res = service.actualizar(1L, usuarioId, req);
        assertEquals(manana, res.fecha());
    }

    @Test
    void testActualizarNoEncontradoLanzaExcepcion() {
        InicioMenstrualRequest req = new InicioMenstrualRequest(hoy);
        when(repository.findByIdAndUsuarioId(99L, usuarioId)).thenReturn(Optional.empty());

        assertThrows(RegistroNoEncontradoException.class, () -> service.actualizar(99L, usuarioId, req));
    }

    @Test
    void testActualizarFechaDuplicadaLanzaExcepcion() {
        InicioMenstrual entidad = new InicioMenstrual(1L, usuarioId, hoy);
        LocalDate manana = hoy.plusDays(1);
        InicioMenstrualRequest req = new InicioMenstrualRequest(manana);

        when(repository.findByIdAndUsuarioId(1L, usuarioId)).thenReturn(Optional.of(entidad));
        when(repository.existsByUsuarioIdAndFecha(usuarioId, manana)).thenReturn(true);

        assertThrows(DataIntegrityViolationException.class, () -> service.actualizar(1L, usuarioId, req));
    }

    @Test
    void testEliminarExitoso() {
        InicioMenstrual entidad = new InicioMenstrual(1L, usuarioId, hoy);
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
