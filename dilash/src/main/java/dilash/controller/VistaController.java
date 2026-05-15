package dilash.controller;

import dilash.model.EstadoCita;
import dilash.model.Usuario;
import dilash.service.AdminService;
import dilash.service.CitaService;
import dilash.service.EstadoCitaService;
import dilash.service.HorarioService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;

@Controller
public class VistaController {

    @Autowired private AdminService adminService;
    @Autowired private CitaService citaService;
    @Autowired private EstadoCitaService estadoCitaService;
    @Autowired private HorarioService horarioService;

    /**
     * Panel de administración: carga todas las citas con estadísticas de ingresos.
     */
    @GetMapping("/admin")
    public String admin(HttpSession session, Model model) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuario == null || !"ADMIN".equals(usuario.getTipoUsuario())) {
            return "redirect:/login";
        }

        Map<String, Object> adminData = adminService.obtenerDatosPanel();

        model.addAttribute("citas", adminData.get("citas"));
        model.addAttribute("totalCitas", adminData.get("totalCitas"));
        model.addAttribute("agendadas", adminData.get("agendadas"));
        model.addAttribute("realizadas", adminData.get("realizadas"));
        model.addAttribute("ingresos", adminData.get("ingresos"));
        
        model.addAttribute("estados", estadoCitaService.getTodos());
        model.addAttribute("horarios", horarioService.getTodos());
        model.addAttribute("usuario", usuario);

        return "admin";
    }

    /**
     * Obtiene los horarios ocupados de una fecha específica (para verificar disponibilidad).
     */
    @GetMapping("/api/citas/horarios-ocupados")
    @ResponseBody
    public List<Long> obtenerHorariosOcupados(@RequestParam String fecha) {
        return citaService.obtenerHorariosOcupados(fecha);
    }

    /**
     * Cambia el estado de una cita (Agendada, Realizada, Cancelada).
     */
    @PostMapping("/admin/estado")
    public String cambiarEstado(@RequestParam Integer idCita,
                                @RequestParam Integer idEstado,
                                HttpSession session,
                                RedirectAttributes ra) {
        if (!esAdmin(session)) return "redirect:/login";

        try {
            adminService.cambiarEstado(idCita, idEstado);
            EstadoCita estado = estadoCitaService.obtenerPorId(idEstado).orElse(null);
            ra.addFlashAttribute("exito", "Estado actualizado a \"" + (estado != null ? estado.getNombre() : "?") + "\".");
        } catch (IllegalArgumentException e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin";
    }

    /**
     * Reagenda una cita con nueva fecha, hora y observaciones.
     * Automáticamente vuelve al estado "Agendada" al reagendar.
     */
    @PostMapping("/admin/reagendar")
    public String reagendar(@RequestParam Integer idCita,
                            @RequestParam String fecha,
                            @RequestParam Long idHorario,
                            @RequestParam(required = false) String observaciones,
                            HttpSession session,
                            RedirectAttributes ra) {
        if (!esAdmin(session)) return "redirect:/login";

        try {
            String mensaje = adminService.reagendarCita(idCita, fecha, idHorario, observaciones);
            ra.addFlashAttribute("exito", mensaje);
        } catch (IllegalArgumentException | IllegalStateException e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin";
    }

    /**
     * Elimina una cita y todos sus detalles de servicios.
     */
    @PostMapping("/admin/eliminar")
    public String eliminarCita(@RequestParam Integer idCita,
                            HttpSession session,
                            RedirectAttributes ra) {
        if (!esAdmin(session)) return "redirect:/login";
        try {
            adminService.eliminarCita(idCita);
            ra.addFlashAttribute("exito", "Cita #" + idCita + " eliminada.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "No se pudo eliminar: " + e.getMessage());
        }
        return "redirect:/admin";
    }

    /**
     * Verifica si el usuario logueado es admin.
     */
    private boolean esAdmin(HttpSession session) {
        Usuario u = (Usuario) session.getAttribute("usuarioLogueado");
        return u != null && "ADMIN".equals(u.getTipoUsuario());
    }
}
