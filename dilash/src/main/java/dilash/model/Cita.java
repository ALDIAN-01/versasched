package dilash.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Entidad Cita — alineada con dilash-clean.sql
 *
 * CAMBIOS VS. VERSIÓN ANTERIOR:
 *  - Eliminados: nombreCliente, telefonoCliente, apellidoCliente, correoCliente
 *    (el cliente ya vive en la tabla `usuario`; no hay columnas duplicadas en `cita`).
 *  - Eliminado: campo `hora` (LocalTime). La hora ahora es FK → tabla `horario`.
 *  - Añadida relación @ManyToOne con Horario (id_horario).
 *  - `total` es insertable=false / updatable=false porque lo gestiona el trigger
 *    TR_ActualizarTotalCita; la app NO debe escribir ese campo.
 *  - tipo de `total` cambiado a BigDecimal para coincidir con DECIMAL(18,2) del SQL.
 */
@Entity
@Table(name = "cita")
public class Cita {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_cita")
    private Integer idCita;

    // ── Relaciones FK ─────────────────────────────────────────────────────────

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_estado", nullable = false)
    private EstadoCita estado;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_horario")
    private Horario horario;

    // ── Campos propios ────────────────────────────────────────────────────────

    @Column(nullable = false)
    private LocalDate fecha;

    private String lugar;

    @Column(name = "direccion_servicio")
    private String direccionServicio;

    private String observaciones;

    /** Gestionado exclusivamente por el trigger TR_ActualizarTotalCita */
    @Column(insertable = false, updatable = false)
    private BigDecimal total;

    @OneToMany(mappedBy = "cita", fetch = FetchType.EAGER)
    private List<DetalleCita> detalles;

    // ── Getters / Setters ─────────────────────────────────────────────────────

    public Integer getIdCita() { return idCita; }
    public void setIdCita(Integer idCita) { this.idCita = idCita; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }

    public EstadoCita getEstado() { return estado; }
    public void setEstado(EstadoCita estado) { this.estado = estado; }

    public Horario getHorario() { return horario; }
    public void setHorario(Horario horario) { this.horario = horario; }

    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }

    public String getLugar() { return lugar; }
    public void setLugar(String lugar) { this.lugar = lugar; }

    public String getDireccionServicio() { return direccionServicio; }
    public void setDireccionServicio(String direccionServicio) { this.direccionServicio = direccionServicio; }

    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }

    public BigDecimal getTotal() { return total; }
    // No hay setter de total — el trigger es el único que lo actualiza.

    public List<DetalleCita> getDetalles() { return detalles; }
    public void setDetalles(List<DetalleCita> detalles) { this.detalles = detalles; }
}
