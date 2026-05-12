package dilash.model;

import jakarta.persistence.*;

@Entity
@Table(name = "detalle_cita")
public class DetalleCita {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idDetalle;

    @ManyToOne
    @JoinColumn(name = "id_cita")
    private Cita cita;

    @ManyToOne
    @JoinColumn(name = "id_servicio")
    private Servicio servicio;

    private Double precioAplicado;

    // getters y setters
    public Long getIdDetalle() {
        return idDetalle;
    }

    public void setIdDetalle(Long idDetalle) {
        this.idDetalle = idDetalle;
    }

    public Cita getCita() {
        return cita;
    }

    public void setCita(Cita cita) {
        this.cita = cita;
    }

    public Servicio getServicio() {
        return servicio;
    }

    public void setServicio(Servicio servicio) {
        this.servicio = servicio;
    }

    public Double getPrecioAplicado() {
        return precioAplicado;
    }

    public void setPrecioAplicado(Double precioAplicado) {
        this.precioAplicado = precioAplicado;
    }

    
}
