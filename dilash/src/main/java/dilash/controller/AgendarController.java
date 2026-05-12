package dilash.controller;

import dilash.model.Horario;
import dilash.model.Usuario;
import dilash.repository.HorarioRepository;
import dilash.repository.ServicioRepository;
import dilash.service.CitaService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * AgendarController — reescrito para usar los SP definidos en dilash-clean.sql
 *
 * CAMBIOS VS. VERSIÓN ANTERIOR:
 *  - GET /agendar: ya no filtra por LocalTime. Filtra por id_horario usando
 *    CitaService.getHorariosDisponibles(). El modelo expone objetos Horario
 *    completos (idHorario + hora) para que la vista pueda usar el ID como value
 *    y la hora como texto visible.
 *  - POST /agendar:
 *      · Recibe `idHorario` (Long) en lugar de `hora` (String).
 *      · NO construye un objeto Cita manualmente — llama a sp_crear_cita vía
 *        CitaService.crearCita(). El SP se encarga del estado inicial (Agendada).
 *      · Itera los servicios y llama a sp_agregar_servicio_cita. El trigger SQL
 *        actualiza el total; la app no lo toca.
 *      · Eliminados: nombreCliente, telefonoCliente, apellidoCliente, correoCliente
 *        (no existen en la tabla `cita` del nuevo esquema).
 */
@Controller
public class AgendarController {

    @Autowired
    private CitaService citaService;

    @Autowired
    private HorarioRepository horarioRepository;

    @Autowired
    private ServicioRepository servicioRepository;

    // ── GET /agendar ──────────────────────────────────────────────────────────

    @GetMapping("/agendar")
    public String verAgendar(@RequestParam(required = false) String fecha,
                            HttpSession session, Model model) {

        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuario == null) return "redirect:/login";

        model.addAttribute("usuario", usuario);
        model.addAttribute("servicios", servicioRepository.findAll()); // ← AÑADE ESTA LÍNEA

        List<Horario> horarios;
        if (fecha != null && !fecha.isBlank()) {
            horarios = citaService.getHorariosDisponibles(LocalDate.parse(fecha));
        } else {
            horarios = horarioRepository.findAll();
        }
        model.addAttribute("horarios", horarios);
        return "agendar";
    }

    // ── POST /agendar ─────────────────────────────────────────────────────────

    @PostMapping("/agendar")
    public String guardar(@RequestParam String fecha,
                          @RequestParam Long idHorario,
                          @RequestParam(required = false) String lugar,
                          @RequestParam(required = false) String observaciones,
                          @RequestParam(required = false) String direccion,
                          @RequestParam(required = false) List<Long> servicios,
                          Model model, HttpSession session) {

        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuario == null) {
            return "redirect:/login";
        }

        LocalDate fechaParsed = LocalDate.parse(fecha);

        // Determinar dirección según lugar
        String direccionFinal = null;
        if ("Mi direccion".equals(lugar) && direccion != null && !direccion.isBlank()) {
            direccionFinal = direccion;
        } else if ("Local".equals(lugar)) {
            direccionFinal = "Local Dilash";
        }

        // 1. Crear cabecera de cita via SP (estado inicial = Agendada, id=1)
        Long idCita;
        try {
            idCita = citaService.crearCita(usuario.getIdUsuario(), fechaParsed, idHorario, direccionFinal, lugar, observaciones);
        } catch (Exception e) {
            // Volvemos a cargar los datos necesarios para la vista sin redireccionar
            model.addAttribute("usuario", usuario);
            model.addAttribute("servicios", servicioRepository.findAll());
            model.addAttribute("horarios", citaService.getHorariosDisponibles(fechaParsed));
            model.addAttribute("error", "Ese horario ya fue tomado. Por favor, elige otro.");
            return "agendar"; // Retorna el template, manteniendo los datos en los inputs
        }

        // 2. Agregar cada servicio via SP (el trigger actualiza el total automáticamente)
        if (servicios != null) {
            for (Long idServicio : servicios) {
                citaService.agregarServicio(idCita, idServicio);
            }
        }

        return "redirect:/cliente";
    }
}
