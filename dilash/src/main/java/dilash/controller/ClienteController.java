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

    /**
     * Muestra el perfil del cliente con sus citas agendadas, realizadas o canceladas.
     */
    @GetMapping("/cliente")
    public String cliente(HttpSession session, Model model) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuario == null) return "redirect:/login";

        List<Cita> citas = citaRepository.findByUsuarioOrderByFechaDesc(usuario);
        model.addAttribute("usuario", usuario);
        model.addAttribute("citas", citas);
        return "cliente";
    }
}
