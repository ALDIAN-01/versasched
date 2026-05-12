package dilash.repository;

import dilash.model.Cita;
import dilash.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

public interface CitaRepository extends JpaRepository<Cita, Long> {

    @Query("SELECT c.horario.idHorario FROM Cita c WHERE c.fecha = :fecha AND c.estado.idEstado != 3")
    List<Long> findHorariosOcupados(@Param("fecha") LocalDate fecha);
    List<Cita> findByUsuarioOrderByFechaDesc(Usuario usuario);

    /**
     * Elimina los detalles de una cita antes de eliminar la cita misma
     * (necesario por la FK de detalle_cita → cita).
     */
    @Modifying
    @Transactional
    @Query(value = "DELETE FROM detalle_cita WHERE id_cita = :idCita", nativeQuery = true)
    void eliminarDetallesPorCita(@Param("idCita") Integer idCita);
}
