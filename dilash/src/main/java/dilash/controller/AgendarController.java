package dilash.controller;

import dilash.model.Usuario;
import dilash.service.CitaService;
import dilash.service.ServicioService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;


@Controller
public class AgendarController {

    @Autowired
    private CitaService citaService;

    @Autowired
    private ServicioService servicioService;

    // ── GET /agendar ──────────────────────────────────────────────────────────

    @GetMapping("/agendar")
    public String verAgendar(@RequestParam(required = false) String fecha,
                            HttpSession session, Model model) {

        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuario == null) return "redirect:/login";

        model.addAttribute("usuario", usuario);
        model.addAttribute("servicios", servicioService.getTodos());
        model.addAttribute("horarios", citaService.obtenerHorariosDisponibles(fecha));
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

        Long idCita;
        try {
            idCita = citaService.reservarCita(usuario.getIdUsuario(), fecha, idHorario, lugar, direccion, observaciones, servicios);
        } catch (Exception e) {
            model.addAttribute("usuario", usuario);
            model.addAttribute("servicios", servicioService.getTodos());
            model.addAttribute("horarios", citaService.obtenerHorariosDisponibles(fecha));
            model.addAttribute("error", "Ese horario ya fue tomado. Por favor, elige otro.");
            return "agendar";
        }

        return "redirect:/cliente";
    }
}
