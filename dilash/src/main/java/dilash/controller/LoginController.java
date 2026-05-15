package dilash.controller;

import dilash.model.Usuario;
import dilash.service.UsuarioService;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class LoginController {

    @Autowired
    private UsuarioService usuarioService;

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

        Usuario usuario = usuarioService.autenticar(correo, contrasena);

        if (usuario != null) {

            session.setAttribute("usuarioLogueado", usuario);

            // Redirección
            String redirectUrl = usuarioService.determinarRedireccionPostLogin(usuario, redirect);
            return "redirect:" + redirectUrl;

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