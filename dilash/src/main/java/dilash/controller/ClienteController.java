package dilash.controller;

import dilash.model.Cita;
import dilash.model.EstadoCita;
import dilash.model.Usuario;
import dilash.repository.CitaRepository;
import dilash.repository.EstadoCitaRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class ClienteController {

    @Autowired private CitaRepository citaRepository;
    @Autowired private EstadoCitaRepository estadoCitaRepository;

    // ── GET /cliente ──────────────────────────────────────────────────────────

    @GetMapping("/cliente")
    public String cliente(HttpSession session, Model model) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuario == null) return "redirect:/login";

        List<Cita> citas = citaRepository.findByUsuarioOrderByFechaDesc(usuario);
        model.addAttribute("usuario", usuario);
        model.addAttribute("citas", citas);
        return "cliente";
    }

    // ── POST /cliente/cancelar ────────────────────────────────────────────────
    // El cliente solo puede cancelar sus propias citas en estado "Agendada".

    @Transactional
    @PostMapping("/cliente/cancelar")
    public String cancelar(@RequestParam Integer idCita,
                           @RequestParam(required = false) String motivoCancelacion,
                           @RequestParam(required = false) Boolean quiereReagendar,
                           HttpSession session,
                           RedirectAttributes ra) {

        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuario == null) return "redirect:/login";

        Cita cita = citaRepository.findById(idCita.longValue()).orElse(null);

        // Seguridad: verificar que la cita pertenece al usuario logueado
        if (cita == null || !cita.getUsuario().getIdUsuario().equals(usuario.getIdUsuario())) {
            ra.addFlashAttribute("error", "No se encontró la cita.");
            return "redirect:/cliente";
        }

        // Solo cancelar si está Agendada (id=1) — usar .equals() para Integer seguro
        if (!Integer.valueOf(1).equals(cita.getEstado().getIdEstado())) {
            ra.addFlashAttribute("error", "Solo puedes cancelar citas que estén agendadas.");
            return "redirect:/cliente";
        }

        EstadoCita cancelado = estadoCitaRepository.findByNombre("Cancelada").orElse(null);
        if (cancelado == null) {
            ra.addFlashAttribute("error", "No se pudo cancelar la cita. Estado 'Cancelada' no disponible.");
            return "redirect:/cliente";
        }

        String motivo = motivoCancelacion != null ? motivoCancelacion.trim() : "";
        String mensaje = "Motivo cancelación: " + (motivo.isBlank() ? "No especificado" : motivo);
        mensaje += quiereReagendar != null && quiereReagendar ? " | Cliente solicita reagendar." : " | Cliente no desea reagendar.";
        if (cita.getObservaciones() != null && !cita.getObservaciones().isBlank()) {
            cita.setObservaciones(cita.getObservaciones().trim() + " | " + mensaje);
        } else {
            cita.setObservaciones(mensaje);
        }

        cita.setEstado(cancelado);
        citaRepository.save(cita);
        ra.addFlashAttribute("exito", "Tu cita fue marcada como cancelada. El admin podrá revisarla.");
        return "redirect:/cliente";
    }
}
