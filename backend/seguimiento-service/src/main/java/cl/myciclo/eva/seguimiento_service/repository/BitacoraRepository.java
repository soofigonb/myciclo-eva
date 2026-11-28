package cl.myciclo.eva.seguimiento_service.repository;

import cl.myciclo.eva.seguimiento_service.model.Bitacora;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface BitacoraRepository extends JpaRepository<Bitacora, Long> {

    List<Bitacora> findByUsuarioIdOrderByFechaDesc(Long usuarioId);

    Optional<Bitacora> findByIdAndUsuarioId(Long id, Long usuarioId);

    Optional<Bitacora> findByUsuarioIdAndFecha(Long usuarioId, LocalDate fecha);

    boolean existsByUsuarioIdAndFecha(Long usuarioId, LocalDate fecha);

    void deleteByIdAndUsuarioId(Long id, Long usuarioId);
}
