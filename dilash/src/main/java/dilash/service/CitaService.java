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
 * Servicio de Citas — orquesta los Stored Procedures definidos en dilash-clean.sql
 *
 * PROCEDIMIENTOS USADOS:
 *  - sp_crear_cita           → crea la cabecera de la cita (estado=Agendada automáticamente)
 *  - sp_agregar_servicio_cita → inserta en detalle_cita (el trigger actualiza total)
 *  - sp_consultar_todas_las_citas → vista del panel Admin
 */
@Service
public class CitaService {

    @Autowired
    private JdbcTemplate jdbc;

    @Autowired
    private CitaRepository citaRepository;

    @Autowired
    private HorarioRepository horarioRepository;

    // ── Horarios disponibles ───────────────────────────────────────────────────

    /**
     * Retorna los objetos Horario que aún NO están ocupados en la fecha indicada.
     */
    public List<Horario> getHorariosDisponibles(LocalDate fecha) {
        List<Long> ocupados = citaRepository.findHorariosOcupados(fecha);
        return horarioRepository.findAll()
                .stream()
                .filter(h -> !ocupados.contains(h.getIdHorario()))
                .collect(Collectors.toList());
    }

    // ── Creación de cita via SP ────────────────────────────────────────────────

    /**
     * Llama a sp_crear_cita y retorna el id_cita generado.
     *
     * @param idUsuario          FK del usuario logueado
     * @param fecha              Fecha de la cita
     * @param idHorario          FK del horario seleccionado
     * @param direccionServicio  Dirección (null si es en Local)
     * @param lugar              "Local" | "Mi direccion"
     * @param observaciones      Texto libre (nullable)
     * @return id de la cita recién creada
     * @throws RuntimeException si el horario ya está ocupado (el SP lanza RAISERROR)
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

        // El SP retorna: SELECT SCOPE_IDENTITY() AS id_cita_creada
        Number idCita = (Number) row.get("id_cita_creada");
        return idCita.longValue();
    }

    // ── Agregar servicio a cita via SP ─────────────────────────────────────────

    /**
     * Llama a sp_agregar_servicio_cita para insertar un servicio en detalle_cita.
     * El trigger TR_ActualizarTotalCita recalcula automáticamente el total de la cita.
     */
    @Transactional
    public void agregarServicio(Long idCita, Long idServicio) {
        String sql = "EXEC sp_agregar_servicio_cita @id_cita=?, @id_servicio=?";
        jdbc.execute(String.format("EXEC sp_agregar_servicio_cita @id_cita=%d, @id_servicio=%d",
                idCita, idServicio));
    }

    // ── Panel Admin: todas las citas via SP ────────────────────────────────────

    /**
     * Llama a sp_consultar_todas_las_citas.
     * Retorna lista de mapas con: id_cita, cliente, fecha, hora, estado, total.
     */
    public List<Map<String, Object>> consultarTodasLasCitas() {
        return normalizeKeys(jdbc.queryForList("EXEC sp_consultar_todas_las_citas"));
    }

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
