package cl.myciclo.eva.seguimiento_service.service;

import cl.myciclo.eva.seguimiento_service.dto.InicioMenstrualRequest;
import cl.myciclo.eva.seguimiento_service.dto.InicioMenstrualResponse;
import cl.myciclo.eva.seguimiento_service.exception.RegistroNoEncontradoException;
import cl.myciclo.eva.seguimiento_service.model.InicioMenstrual;
import cl.myciclo.eva.seguimiento_service.repository.InicioMenstrualRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class InicioMenstrualService {

    private final InicioMenstrualRepository repository;

    public InicioMenstrualService(InicioMenstrualRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<InicioMenstrualResponse> listarPorUsuario(Long usuarioId) {
        return repository.findByUsuarioIdOrderByFechaDesc(usuarioId)
                .stream()
                .map(this::mapearAResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public InicioMenstrualResponse obtenerPorId(Long id, Long usuarioId) {
        InicioMenstrual entidad = repository.findByIdAndUsuarioId(id, usuarioId)
                .orElseThrow(() -> new RegistroNoEncontradoException(
                        "No existe el inicio menstrual con ID " + id + " o pertenece a otra cuenta."
                ));
        return mapearAResponse(entidad);
    }

    public InicioMenstrualResponse crear(Long usuarioId, InicioMenstrualRequest request) {
        if (repository.existsByUsuarioIdAndFecha(usuarioId, request.fecha())) {
            throw new DataIntegrityViolationException(
                    "Ya registraste un inicio menstrual para la fecha " + request.fecha()
            );
        }

        InicioMenstrual entidad = new InicioMenstrual(null, usuarioId, request.fecha());
        InicioMenstrual guardado = repository.save(entidad);
        return mapearAResponse(guardado);
    }

    public InicioMenstrualResponse actualizar(Long id, Long usuarioId, InicioMenstrualRequest request) {
        InicioMenstrual existente = repository.findByIdAndUsuarioId(id, usuarioId)
                .orElseThrow(() -> new RegistroNoEncontradoException(
                        "No existe el inicio menstrual con ID " + id + " o pertenece a otra cuenta."
                ));

        if (!existente.getFecha().equals(request.fecha()) &&
                repository.existsByUsuarioIdAndFecha(usuarioId, request.fecha())) {
            throw new DataIntegrityViolationException(
                    "Ya existe otro registro para la fecha " + request.fecha()
            );
        }

        existente.setFecha(request.fecha());
        InicioMenstrual guardado = repository.save(existente);
        return mapearAResponse(guardado);
    }

    public void eliminar(Long id, Long usuarioId) {
        InicioMenstrual existente = repository.findByIdAndUsuarioId(id, usuarioId)
                .orElseThrow(() -> new RegistroNoEncontradoException(
                        "No existe el inicio menstrual con ID " + id + " o pertenece a otra cuenta."
                ));
        repository.delete(existente);
    }

    private InicioMenstrualResponse mapearAResponse(InicioMenstrual entidad) {
        return new InicioMenstrualResponse(
                entidad.getId(),
                entidad.getUsuarioId(),
                entidad.getFecha()
        );
    }
}
