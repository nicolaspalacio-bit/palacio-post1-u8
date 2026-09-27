package com.example.auditoria.domain.entity;

import com.example.auditoria.domain.valueobject.EstadoHallazgo;
import com.example.auditoria.domain.valueobject.HallazgoId;
import com.example.auditoria.domain.valueobject.PlanRemediacion;
import com.example.auditoria.domain.valueobject.Severidad;
import com.example.auditoria.domain.valueobject.TransicionInvalidaException;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Aggregate Root. El plan de remediacion vive dentro del agregado: las invariantes
 * "no hay remediacion sin plan" y "no se cierra sin plan" se cumplen siempre en una
 * misma operacion, sin ventanas de inconsistencia.
 */
public class HallazgoAuditoria {

    private final HallazgoId id;
    private final String titulo;
    private final String descripcion;
    private final String areaResponsable;
    private final Severidad severidad;
    private final LocalDate fechaDeteccion;
    private EstadoHallazgo estado;
    private PlanRemediacion planRemediacion;
    private LocalDate fechaCierre;

    /** Registro de un hallazgo nuevo: siempre nace ABIERTO. */
    public HallazgoAuditoria(HallazgoId id, String titulo, String descripcion,
                              String areaResponsable, Severidad severidad, LocalDate fechaDeteccion) {
        this(id, titulo, descripcion, areaResponsable, severidad, fechaDeteccion,
            EstadoHallazgo.ABIERTO, null, null);
    }

    private HallazgoAuditoria(HallazgoId id, String titulo, String descripcion,
                               String areaResponsable, Severidad severidad, LocalDate fechaDeteccion,
                               EstadoHallazgo estado, PlanRemediacion plan, LocalDate fechaCierre) {
        Objects.requireNonNull(id);
        if (titulo == null || titulo.isBlank())
            throw new IllegalArgumentException("El titulo es obligatorio");
        if (areaResponsable == null || areaResponsable.isBlank())
            throw new IllegalArgumentException("El area responsable es obligatoria");
        Objects.requireNonNull(severidad, "La severidad es obligatoria");
        Objects.requireNonNull(fechaDeteccion, "La fecha de deteccion es obligatoria");
        Objects.requireNonNull(estado);
        if (estado != EstadoHallazgo.ABIERTO && plan == null)
            throw new IllegalStateException("Un hallazgo en estado " + estado + " debe tener plan de remediacion");
        if (estado == EstadoHallazgo.CERRADO && fechaCierre == null)
            throw new IllegalStateException("Un hallazgo CERRADO debe tener fecha de cierre");
        this.id = id;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.areaResponsable = areaResponsable;
        this.severidad = severidad;
        this.fechaDeteccion = fechaDeteccion;
        this.estado = estado;
        this.planRemediacion = plan;
        this.fechaCierre = fechaCierre;
    }

    /**
     * Rehidrata un hallazgo ya existente tal como fue persistido, sin volver a ejecutar
     * sus transiciones (que alterarian la fecha de cierre y otros datos historicos).
     */
    public static HallazgoAuditoria reconstituir(HallazgoId id, String titulo, String descripcion,
                                                  String areaResponsable, Severidad severidad,
                                                  LocalDate fechaDeteccion, EstadoHallazgo estado,
                                                  PlanRemediacion plan, LocalDate fechaCierre) {
        return new HallazgoAuditoria(id, titulo, descripcion, areaResponsable, severidad,
            fechaDeteccion, estado, plan, fechaCierre);
    }

    public EstadoHallazgo iniciarRemediacion(PlanRemediacion plan) {
        Objects.requireNonNull(plan, "El plan de remediacion es obligatorio");
        EstadoHallazgo anterior = transicionar(EstadoHallazgo.EN_REMEDIACION);
        this.planRemediacion = plan;
        return anterior;
    }

    public EstadoHallazgo cerrar() {
        if (planRemediacion == null)
            throw new IllegalStateException("No se puede cerrar un hallazgo sin plan de remediacion");
        EstadoHallazgo anterior = transicionar(EstadoHallazgo.CERRADO);
        this.fechaCierre = LocalDate.now();
        return anterior;
    }

    public EstadoHallazgo reabrir() {
        EstadoHallazgo anterior = transicionar(EstadoHallazgo.REABIERTO);
        this.fechaCierre = null;
        return anterior;
    }

    private EstadoHallazgo transicionar(EstadoHallazgo destino) {
        if (!estado.puedeTransicionarA(destino))
            throw new TransicionInvalidaException(estado, destino);
        EstadoHallazgo anterior = this.estado;
        this.estado = destino;
        return anterior;
    }

    // Solo getters: el estado cambia unicamente mediante metodos de dominio.
    public HallazgoId getId() { return id; }
    public String getTitulo() { return titulo; }
    public String getDescripcion() { return descripcion; }
    public String getAreaResponsable() { return areaResponsable; }
    public Severidad getSeveridad() { return severidad; }
    public LocalDate getFechaDeteccion() { return fechaDeteccion; }
    public EstadoHallazgo getEstado() { return estado; }
    public PlanRemediacion getPlanRemediacion() { return planRemediacion; }
    public LocalDate getFechaCierre() { return fechaCierre; }
}
