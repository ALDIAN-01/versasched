package dilash.service;

import dilash.model.Horario;
import dilash.model.EstadoCita;
import dilash.model.Cita;
import dilash.model.Usuario;
import dilash.repository.CitaRepository;
import dilash.repository.EstadoCitaRepository;
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
    @Autowired private EstadoCitaRepository estadoCitaRepository;
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
     * Obtiene horarios disponibles usando un parámetro de fecha.
     */
    public List<Horario> obtenerHorariosDisponibles(String fecha) {
        if (fecha == null || fecha.isBlank()) {
            return horarioRepository.findAll();
        }
        try {
            return getHorariosDisponibles(LocalDate.parse(fecha));
        } catch (Exception e) {
            return horarioRepository.findAll();
        }
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
     * Obtiene los horarios ocupados para una fecha específica.
     */
    public List<Long> obtenerHorariosOcupados(LocalDate fecha) {
        return citaRepository.findHorariosOcupados(fecha);
    }

    /**
     * Obtiene los horarios ocupados para una fecha en formato String.
     */
    public List<Long> obtenerHorariosOcupados(String fecha) {
        try {
            return obtenerHorariosOcupados(LocalDate.parse(fecha));
        } catch (Exception e) {
            return List.of();
        }
    }

    /**
     * Cambia el estado de una cita.
     */
    @Transactional
    public void cambiarEstadoCita(Integer idCita, Integer idEstado) {
        Cita cita = citaRepository.findById(idCita.longValue()).orElse(null);
        EstadoCita estado = estadoCitaRepository.findById(idEstado).orElse(null);
        if (cita == null || estado == null) {
            throw new IllegalArgumentException("Cita o estado no encontrado.");
        }
        cita.setEstado(estado);
        citaRepository.save(cita);
    }

    /**
     * Reagenda una cita asegurando la disponibilidad del horario.
     */
    @Transactional
    public void reagendarCita(Integer idCita, LocalDate fecha, Long idHorario, String observaciones) {
        Cita cita = citaRepository.findById(idCita.longValue()).orElse(null);
        if (cita == null) {
            throw new IllegalArgumentException("Cita no encontrada.");
        }

        List<Long> ocupados = citaRepository.findHorariosOcupados(fecha);
        Long horarioActual = cita.getHorario() != null ? cita.getHorario().getIdHorario() : -1L;
        boolean mismoDia = fecha.equals(cita.getFecha());
        boolean ocupado = ocupados.stream()
                .anyMatch(id -> id.equals(idHorario) && !(mismoDia && id.equals(horarioActual)));
        if (ocupado) {
            throw new IllegalStateException("Ese horario ya está ocupado para esa fecha.");
        }

        Horario horario = horarioRepository.findById(idHorario).orElse(null);
        if (horario == null) {
            throw new IllegalArgumentException("Horario no encontrado.");
        }

        EstadoCita agendada = estadoCitaRepository.findById(1).orElse(cita.getEstado());

        cita.setFecha(fecha);
        cita.setHorario(horario);
        cita.setEstado(agendada);
        if (observaciones != null) cita.setObservaciones(observaciones.trim());
        citaRepository.save(cita);
    }

    /**
     * Elimina una cita y sus detalles.
     */
    @Transactional
    public void eliminarCita(Integer idCita) {
        citaRepository.eliminarDetallesPorCita(idCita);
        citaRepository.deleteById(idCita.longValue());
    }

    /**
     * Devuelve las citas de un cliente.
     */
    public List<Cita> getCitasPorUsuario(Usuario usuario) {
        return citaRepository.findByUsuarioOrderByFechaDesc(usuario);
    }

    /**
     * Determina la dirección final para el servicio basado en el lugar seleccionado.
     */
    public String determinarDireccionServicio(String lugar, String direccion) {
        if ("Mi direccion".equals(lugar) && direccion != null && !direccion.trim().isEmpty()) {
            return direccion.trim();
        } else if ("Local".equals(lugar)) {
            return "Local Dilash";
        }
        return null; // Opcional: manejar otros casos si es necesario
    }

    /**
     * Crea una cita con servicios y determina la dirección final.
     */
    @Transactional
    public Long reservarCita(Long idUsuario,
                            String fecha,
                            Long idHorario,
                            String lugar,
                            String direccion,
                            String observaciones,
                            List<Long> servicios) {
        LocalDate fechaParsed = LocalDate.parse(fecha);
        String direccionFinal = determinarDireccionServicio(lugar, direccion);

        Long idCita = crearCita(idUsuario, fechaParsed, idHorario, direccionFinal, lugar, observaciones);
        if (servicios != null) {
            for (Long idServicio : servicios) {
                agregarServicio(idCita, idServicio);
            }
        }
        return idCita;
    }

    /**
     * Obtiene los datos requeridos por el panel de administración.
     */
    public Map<String, Object> obtenerPanelAdmin() {
        List<Map<String, Object>> citas = consultarTodasLasCitas();
        Map<String, Object> stats = new HashMap<>();
        stats.put("citas", citas);
        stats.put("totalCitas", citas.size());
        stats.put("agendadas", citas.stream().filter(c -> "Agendada".equals(c.get("estado"))).count());
        stats.put("realizadas", citas.stream().filter(c -> "Realizada".equals(c.get("estado"))).count());
        stats.put("ingresos", citas.stream()
                .filter(c -> "Realizada".equals(c.get("estado")))
                .mapToDouble(c -> {
                    Object t = c.get("total");
                    return (t != null) ? Double.parseDouble(t.toString()) : 0.0;
                }).sum());
        return stats;
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
