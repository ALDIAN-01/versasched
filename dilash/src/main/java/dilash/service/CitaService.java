package dilash.service;

import dilash.model.Horario;
import dilash.repository.CitaRepository;
import dilash.repository.HorarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * CitaService
 *
 * Orquesta los Stored Procedures de SQL Server para crear, consultar y gestionar citas.
 * Procedimientos usados:
 *  - sp_crear_cita: crea la cita en estado "Agendada"
 *  - sp_agregar_servicio_cita: añade servicios a la cita (trigger actualiza total)
 *  - sp_consultar_todas_las_citas: consulta el panel admin
 */
@Service
public class CitaService {

    @Autowired private JdbcTemplate jdbc;
    @Autowired private CitaRepository citaRepository;
    @Autowired private HorarioRepository horarioRepository;

    /**
     * Obtiene horarios disponibles para una fecha específica.
     */
    public List<Horario> getHorariosDisponibles(LocalDate fecha) {
        List<Long> ocupados = citaRepository.findHorariosOcupados(fecha);
        return horarioRepository.findAll()
                .stream()
                .filter(h -> !ocupados.contains(h.getIdHorario()))
                .collect(Collectors.toList());
    }

    /**
     * Crea una nueva cita ejecutando el SP sp_crear_cita.
     * Retorna el ID de la cita creada.
     */
    @Transactional
    public Long crearCita(Long idUsuario, LocalDate fecha, Long idHorario,
                          String direccionServicio, String lugar, String observaciones) {

        String sql = "EXEC sp_crear_cita @id_usuario=?, @fecha=?, @id_horario=?, " +
                     "@direccion_servicio=?, @lugar=?, @observaciones=?";

        Map<String, Object> row = jdbc.queryForMap(sql,
                idUsuario,
                java.sql.Date.valueOf(fecha),
                idHorario,
                direccionServicio,
                lugar,
                observaciones);

        Number idCita = (Number) row.get("id_cita_creada");
        return idCita.longValue();
    }

    /**
     * Añade un servicio a una cita ejecutando sp_agregar_servicio_cita.
     * El trigger de base de datos actualiza automáticamente el total.
     */
    @Transactional
    public void agregarServicio(Long idCita, Long idServicio) {
        String sql = "EXEC sp_agregar_servicio_cita @id_cita=?, @id_servicio=?";
        jdbc.execute(String.format("EXEC sp_agregar_servicio_cita @id_cita=%d, @id_servicio=%d",
                idCita, idServicio));
    }

    /**
     * Obtiene todas las citas para el panel de administración.
     * Retorna: [id_cita, cliente, fecha, hora, estado, total, observaciones]
     */
    public List<Map<String, Object>> consultarTodasLasCitas() {
        return normalizeKeys(jdbc.queryForList("EXEC sp_consultar_todas_las_citas"));
    }

    /**
     * Normaliza las claves del mapa de resultados a minúsculas
     * (normaliza resultados de SQL Server).
     */

    private List<Map<String, Object>> normalizeKeys(List<Map<String, Object>> rows) {
        List<Map<String, Object>> result = new ArrayList<>();
        for (Map<String, Object> row : rows) {
            Map<String, Object> normalized = new HashMap<>();
            for (Map.Entry<String, Object> entry : row.entrySet()) {
                normalized.put(entry.getKey() == null ? null : entry.getKey().toString().toLowerCase(), entry.getValue());
            }
            result.add(normalized);
        }
        return result;
    }
}
