package dilash.controller;

import dilash.model.Usuario;
import dilash.service.ClienteService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class ClienteController {

    @Autowired private ClienteService clienteService;

    /**
     * Muestra el perfil del cliente con sus citas agendadas, realizadas o canceladas.
     */
    @GetMapping("/cliente")
    public String cliente(HttpSession session, Model model) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuario == null) return "redirect:/login";

        model.addAttribute("usuario", usuario);
        model.addAttribute("citas", clienteService.obtenerCitasPorUsuario(usuario));
        return "cliente";
    }
}
