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
        name = "bitacoras",
        uniqueConstraints = {
                @UniqueConstraint(name = "uq_bitacora_usuario_fecha", columnNames = {"usuario_id", "fecha"})
        }
)
public class Bitacora {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "usuario_id", nullable = false)
    private Long usuarioId;

    @Column(name = "fecha", nullable = false)
    private LocalDate fecha;

    @Column(name = "patron")
    private String patron;

    @Column(name = "sintomas", columnDefinition = "TEXT")
    private String sintomas;

    @Column(name = "estado_animo")
    private String estadoAnimo;

    @Column(name = "flujo_menstrual")
    private String flujoMenstrual;

    @Column(name = "flujo_vaginal")
    private String flujoVaginal;

    @Column(name = "notas", columnDefinition = "TEXT")
    private String notas;

    public Bitacora() {
    }

    public Bitacora(Long id, Long usuarioId, LocalDate fecha, String patron, String sintomas,
                    String estadoAnimo, String flujoMenstrual, String flujoVaginal, String notas) {
        this.id = id;
        this.usuarioId = usuarioId;
        this.fecha = fecha;
        this.patron = patron;
        this.sintomas = sintomas;
        this.estadoAnimo = estadoAnimo;
        this.flujoMenstrual = flujoMenstrual;
        this.flujoVaginal = flujoVaginal;
        this.notas = notas;
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

    public String getPatron() {
        return patron;
    }

    public void setPatron(String patron) {
        this.patron = patron;
    }

    public String getSintomas() {
        return sintomas;
    }

    public void setSintomas(String sintomas) {
        this.sintomas = sintomas;
    }

    public String getEstadoAnimo() {
        return estadoAnimo;
    }

    public void setEstadoAnimo(String estadoAnimo) {
        this.estadoAnimo = estadoAnimo;
    }

    public String getFlujoMenstrual() {
        return flujoMenstrual;
    }

    public void setFlujoMenstrual(String flujoMenstrual) {
        this.flujoMenstrual = flujoMenstrual;
    }

    public String getFlujoVaginal() {
        return flujoVaginal;
    }

    public void setFlujoVaginal(String flujoVaginal) {
        this.flujoVaginal = flujoVaginal;
    }

    public String getNotas() {
        return notas;
    }

    public void setNotas(String notas) {
        this.notas = notas;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Bitacora bitacora = (Bitacora) o;
        return Objects.equals(id, bitacora.id) &&
                Objects.equals(usuarioId, bitacora.usuarioId) &&
                Objects.equals(fecha, bitacora.fecha);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, usuarioId, fecha);
    }
}
