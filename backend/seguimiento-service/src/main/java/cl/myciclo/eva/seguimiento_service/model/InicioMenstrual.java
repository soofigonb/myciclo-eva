package cl.myciclo.eva.seguimiento_service.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDate;
import java.util.Objects;

@Entity
@Table(
        name = "inicios_menstruales",
        uniqueConstraints = {
                @UniqueConstraint(name = "uq_inicio_menstrual_usuario_fecha", columnNames = {"usuario_id", "fecha"})
        }
)
public class InicioMenstrual {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "usuario_id", nullable = false)
    private Long usuarioId;

    @Column(name = "fecha", nullable = false)
    private LocalDate fecha;

    public InicioMenstrual() {
    }

    public InicioMenstrual(Long id, Long usuarioId, LocalDate fecha) {
        this.id = id;
        this.usuarioId = usuarioId;
        this.fecha = fecha;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InicioMenstrual that = (InicioMenstrual) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(usuarioId, that.usuarioId) &&
                Objects.equals(fecha, that.fecha);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, usuarioId, fecha);
    }
}
