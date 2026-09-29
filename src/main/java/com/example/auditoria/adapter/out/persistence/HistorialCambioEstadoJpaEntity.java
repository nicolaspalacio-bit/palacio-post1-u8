package com.example.auditoria.adapter.out.persistence;

import com.example.auditoria.domain.valueobject.EstadoHallazgo;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

/**
 * Registro append-only: adrede no expone ningun setter de actualizacion sobre un registro
 * ya insertado, para que "no se puede alterar retroactivamente" sea una propiedad del
 * modelo y no solo una convencion documentada.
 */
@Entity
@Table(name = "historial_cambios_estado")
public class HistorialCambioEstadoJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String hallazgoId;
    @Enumerated(EnumType.STRING)
    private EstadoHallazgo estadoAnterior;
    @Enumerated(EnumType.STRING)
    private EstadoHallazgo estadoNuevo;
    @Column(length = 1000)
    private String motivo;
    private String actor;
    private LocalDateTime fecha;

    protected HistorialCambioEstadoJpaEntity() {
        // constructor vacio exigido por JPA
    }

    public HistorialCambioEstadoJpaEntity(String hallazgoId, EstadoHallazgo estadoAnterior,
            EstadoHallazgo estadoNuevo, String motivo, String actor, LocalDateTime fecha) {
        this.hallazgoId = hallazgoId;
        this.estadoAnterior = estadoAnterior;
        this.estadoNuevo = estadoNuevo;
        this.motivo = motivo;
        this.actor = actor;
        this.fecha = fecha;
    }

    public Long getId() { return id; }
    public String getHallazgoId() { return hallazgoId; }
    public EstadoHallazgo getEstadoAnterior() { return estadoAnterior; }
    public EstadoHallazgo getEstadoNuevo() { return estadoNuevo; }
    public String getMotivo() { return motivo; }
    public String getActor() { return actor; }
    public LocalDateTime getFecha() { return fecha; }
}
