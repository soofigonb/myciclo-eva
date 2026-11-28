package cl.myciclo.eva.seguimiento_service.service;

import cl.myciclo.eva.seguimiento_service.dto.BitacoraRequest;
import cl.myciclo.eva.seguimiento_service.dto.BitacoraResponse;
import cl.myciclo.eva.seguimiento_service.exception.RegistroNoEncontradoException;
import cl.myciclo.eva.seguimiento_service.model.Bitacora;
import cl.myciclo.eva.seguimiento_service.repository.BitacoraRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class BitacoraService {

    private final BitacoraRepository repository;

    public BitacoraService(BitacoraRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<BitacoraResponse> listarPorUsuario(Long usuarioId) {
        return repository.findByUsuarioIdOrderByFechaDesc(usuarioId)
                .stream()
                .map(this::mapearAResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public BitacoraResponse obtenerPorId(Long id, Long usuarioId) {
        Bitacora entidad = repository.findByIdAndUsuarioId(id, usuarioId)
                .orElseThrow(() -> new RegistroNoEncontradoException(
                        "No existe la bitácora con ID " + id + " o pertenece a otra cuenta."
                ));
        return mapearAResponse(entidad);
    }

    public BitacoraResponse crear(Long usuarioId, BitacoraRequest request) {
        if (repository.existsByUsuarioIdAndFecha(usuarioId, request.fecha())) {
            throw new DataIntegrityViolationException(
                    "Ya existe una bitácora registrada para la fecha " + request.fecha()
            );
        }

        Bitacora entidad = new Bitacora(
                null,
                usuarioId,
                request.fecha(),
                request.patron(),
                request.sintomas(),
                request.estadoAnimo(),
                request.flujoMenstrual(),
                request.flujoVaginal(),
                request.notas()
        );

        Bitacora guardado = repository.save(entidad);
        return mapearAResponse(guardado);
    }

    public BitacoraResponse actualizar(Long id, Long usuarioId, BitacoraRequest request) {
        Bitacora existente = repository.findByIdAndUsuarioId(id, usuarioId)
                .orElseThrow(() -> new RegistroNoEncontradoException(
                        "No existe la bitácora con ID " + id + " o pertenece a otra cuenta."
                ));

        if (!existente.getFecha().equals(request.fecha()) &&
                repository.existsByUsuarioIdAndFecha(usuarioId, request.fecha())) {
            throw new DataIntegrityViolationException(
                    "Ya existe otra bitácora para la fecha " + request.fecha()
            );
        }

        existente.setFecha(request.fecha());
        existente.setPatron(request.patron());
        existente.setSintomas(request.sintomas());
        existente.setEstadoAnimo(request.estadoAnimo());
        existente.setFlujoMenstrual(request.flujoMenstrual());
        existente.setFlujoVaginal(request.flujoVaginal());
        existente.setNotas(request.notas());

        Bitacora guardado = repository.save(existente);
        return mapearAResponse(guardado);
    }

    public void eliminar(Long id, Long usuarioId) {
        Bitacora existente = repository.findByIdAndUsuarioId(id, usuarioId)
                .orElseThrow(() -> new RegistroNoEncontradoException(
                        "No existe la bitácora con ID " + id + " o pertenece a otra cuenta."
                ));
        repository.delete(existente);
    }

    private BitacoraResponse mapearAResponse(Bitacora entidad) {
        return new BitacoraResponse(
                entidad.getId(),
                entidad.getUsuarioId(),
                entidad.getFecha(),
                entidad.getPatron(),
                entidad.getSintomas(),
                entidad.getEstadoAnimo(),
                entidad.getFlujoMenstrual(),
                entidad.getFlujoVaginal(),
                entidad.getNotas()
        );
    }
}
