package dilash.controller;

import dilash.model.Cita;
import dilash.model.EstadoCita;
import dilash.model.Horario;
import dilash.model.Usuario;
import dilash.repository.CitaRepository;
import dilash.repository.EstadoCitaRepository;
import dilash.repository.HorarioRepository;
import dilash.service.CitaService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Controller
public class VistaController {

    @Autowired private CitaService citaService;
    @Autowired private CitaRepository citaRepository;
    @Autowired private EstadoCitaRepository estadoCitaRepository;
    @Autowired private HorarioRepository horarioRepository;

    // ── GET /admin ────────────────────────────────────────────────────────────

    @GetMapping("/admin")
    public String admin(HttpSession session, Model model) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuario == null || !"ADMIN".equals(usuario.getTipoUsuario())) {
            return "redirect:/login";
        }

        // 1. Obtener la lista base
        List<Map<String, Object>> citas = citaService.consultarTodasLasCitas();

        // 2. Calcular estadísticas para los cuadros de la imagen 2.png
        long totalCitas = citas.size();
        long agendadas = citas.stream()
                .filter(c -> "Agendada".equals(c.get("estado")))
                .count();
        long realizadas = citas.stream()
                .filter(c -> "Realizada".equals(c.get("estado")))
                .count();
        
        // Sumar el total de ingresos (solo de citas Realizadas)
        double ingresos = citas.stream()
                .filter(c -> "Realizada".equals(c.get("estado")))
                .mapToDouble(c -> {
                    Object t = c.get("total");
                    return (t != null) ? Double.parseDouble(t.toString()) : 0.0;
                })
                .sum();

        // 3. Agregar todo al modelo
        model.addAttribute("citas", citas);
        model.addAttribute("totalCitas", totalCitas);
        model.addAttribute("agendadas", agendadas);
        model.addAttribute("realizadas", realizadas);
        model.addAttribute("ingresos", ingresos);
        
        model.addAttribute("estados", estadoCitaRepository.findAll());
        model.addAttribute("horarios", horarioRepository.findAll());
        model.addAttribute("usuario", usuario);

        return "admin";
    }

    @GetMapping("/api/citas/horarios-ocupados")
    @ResponseBody // Esto hace que devuelva JSON y no una página HTML
    public List<Long> obtenerHorariosOcupados(@RequestParam String fecha) {
        try {
            LocalDate date = LocalDate.parse(fecha);
            return citaRepository.findHorariosOcupados(date);
        } catch (Exception e) {
            return List.of(); // Devuelve lista vacía si hay error
        }
    }

    // ── POST /admin/estado ────────────────────────────────────────────────────

    @PostMapping("/admin/estado")
    public String cambiarEstado(@RequestParam Integer idCita,
                                @RequestParam Integer idEstado,
                                HttpSession session,
                                RedirectAttributes ra) {
        if (!esAdmin(session)) return "redirect:/login";

        Cita cita = citaRepository.findById(idCita.longValue()).orElse(null);
        EstadoCita estado = estadoCitaRepository.findById(idEstado).orElse(null);
        if (cita == null || estado == null) {
            ra.addFlashAttribute("error", "Cita o estado no encontrado.");
            return "redirect:/admin";
        }
        cita.setEstado(estado);
        citaRepository.save(cita);
        ra.addFlashAttribute("exito", "Estado actualizado a \"" + estado.getNombre() + "\".");
        return "redirect:/admin";
    }

    // ── POST /admin/reagendar ─────────────────────────────────────────────────
    // Cambia fecha, horario y observaciones de una cita existente.
    // El estado vuelve a "Agendada" (id=1) automáticamente al reagendar.

    @PostMapping("/admin/reagendar")
    public String reagendar(@RequestParam Integer idCita,
                            @RequestParam String fecha,
                            @RequestParam Long idHorario,
                            @RequestParam(required = false) String observaciones,
                            HttpSession session,
                            RedirectAttributes ra) {
        if (!esAdmin(session)) return "redirect:/login";

        Cita cita = citaRepository.findById(idCita.longValue()).orElse(null);
        if (cita == null) {
            ra.addFlashAttribute("error", "Cita no encontrada.");
            return "redirect:/admin";
        }

        LocalDate nuevaFecha = LocalDate.parse(fecha);

        // Verificar que el nuevo horario no esté ocupado por OTRA cita
        List<Long> ocupados = citaRepository.findHorariosOcupados(nuevaFecha);
        // Excluir el horario actual de esta misma cita (si la fecha no cambia)
        Long horarioActual = cita.getHorario() != null ? cita.getHorario().getIdHorario() : -1L;
        boolean mismoDia   = nuevaFecha.equals(cita.getFecha());
        boolean ocupado    = ocupados.stream()
                .anyMatch(id -> id.equals(idHorario)
                        && !(mismoDia && id.equals(horarioActual)));

        if (ocupado) {
            ra.addFlashAttribute("error", "Ese horario ya está ocupado para esa fecha. Elige otro.");
            return "redirect:/admin";
        }

        Horario horario = horarioRepository.findById(idHorario).orElse(null);
        if (horario == null) {
            ra.addFlashAttribute("error", "Horario no encontrado.");
            return "redirect:/admin";
        }

        // Volver a estado "Agendada" (id=1) al reagendar
        EstadoCita agendada = estadoCitaRepository.findById(1).orElse(cita.getEstado());

        cita.setFecha(nuevaFecha);
        cita.setHorario(horario);
        cita.setEstado(agendada);
        if (observaciones != null) cita.setObservaciones(observaciones.trim());
        citaRepository.save(cita);

        ra.addFlashAttribute("exito",
                "Cita reagendada para el " + nuevaFecha + " a las " + horario.getHora() + ".");
        return "redirect:/admin";
    }

    // ── POST /admin/eliminar ──────────────────────────────────────────────────

    @PostMapping("/admin/eliminar")
    public String eliminarCita(@RequestParam Integer idCita,
                               HttpSession session,
                               RedirectAttributes ra) {
        if (!esAdmin(session)) return "redirect:/login";
        try {
            citaRepository.eliminarDetallesPorCita(idCita);
            citaRepository.deleteById(idCita.longValue());
            ra.addFlashAttribute("exito", "Cita #" + idCita + " eliminada.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "No se pudo eliminar: " + e.getMessage());
        }
        return "redirect:/admin";
    }

    // ── Helper ────────────────────────────────────────────────────────────────

    private boolean esAdmin(HttpSession session) {
        Usuario u = (Usuario) session.getAttribute("usuarioLogueado");
        return u != null && "ADMIN".equals(u.getTipoUsuario());
    }
}
