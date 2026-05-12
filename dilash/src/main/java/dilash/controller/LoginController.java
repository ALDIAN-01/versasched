package dilash.controller;

import dilash.model.Usuario;
import dilash.repository.UsuarioRepository;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class LoginController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @GetMapping("/login")
    public String mostrarLogin() {
        return "login";
    }

    @PostMapping("/login")
    public String procesarLogin(@RequestParam String correo,
                            @RequestParam String contrasena,
                            @RequestParam(required = false) String redirect,
                            HttpSession session,
                            Model model) {

        Usuario usuario = usuarioRepository.findByCorreo(correo);

        if (usuario != null && usuario.getContrasena().equals(contrasena)) {

            session.setAttribute("usuarioLogueado", usuario);

            // 🔥 REDIRECCIÓN INTELIGENTE
            if (redirect != null && !redirect.isEmpty()) {
                return "redirect:" + redirect;
            }

            if (usuario.getTipoUsuario().equals("ADMIN")) {
                return "redirect:/admin";
            } else {
                return "redirect:/cliente";
            }

        } else {
            model.addAttribute("error", "Credenciales incorrectas");
            return "login";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }
}