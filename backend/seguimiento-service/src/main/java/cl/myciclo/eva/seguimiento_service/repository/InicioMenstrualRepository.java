package cl.myciclo.eva.seguimiento_service.repository;

import cl.myciclo.eva.seguimiento_service.model.InicioMenstrual;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface InicioMenstrualRepository extends JpaRepository<InicioMenstrual, Long> {

    List<InicioMenstrual> findByUsuarioIdOrderByFechaDesc(Long usuarioId);

    Optional<InicioMenstrual> findByIdAndUsuarioId(Long id, Long usuarioId);

    Optional<InicioMenstrual> findByUsuarioIdAndFecha(Long usuarioId, LocalDate fecha);

    boolean existsByUsuarioIdAndFecha(Long usuarioId, LocalDate fecha);

    void deleteByIdAndUsuarioId(Long id, Long usuarioId);
}
