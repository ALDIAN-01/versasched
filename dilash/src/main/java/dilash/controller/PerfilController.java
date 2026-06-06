package dilash.controller;

import dilash.model.Usuario;
import dilash.service.PerfilService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * PerfilController — edición de datos personales y contraseña.
 *
 * Rutas:
 *  GET  /perfil               → muestra el formulario
 *  POST /perfil/datos         → actualiza nombre, apellido, correo, teléfono
 *  POST /perfil/contrasena    → cambia la contraseña (verifica la actual)
 */
@Controller
public class PerfilController {

    @Autowired
    private PerfilService perfilService;

    // ── GET /perfil ───────────────────────────────────────────────────────────

    @GetMapping("/perfil")
    public String verPerfil(HttpSession session, Model model) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuario == null) return "redirect:/login";
        usuario = perfilService.obtenerPerfil(usuario.getIdUsuario());
        model.addAttribute("usuario", usuario);
        return "perfil";
    }

    // ── POST /perfil/datos ────────────────────────────────────────────────────

    @PostMapping("/perfil/datos")
    public String actualizarDatos(@RequestParam String nombre,
                                @RequestParam String apellido,
                                @RequestParam String correo,
                                @RequestParam String telefono,
                                HttpSession session,
                                Model model) {

        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuario == null) return "redirect:/login";

        try {
            usuario = perfilService.actualizarPerfil(usuario.getIdUsuario(), nombre, apellido, correo, telefono);
        } catch (IllegalArgumentException e) {
            usuario = perfilService.obtenerPerfil(usuario.getIdUsuario());
            model.addAttribute("usuario", usuario);
            model.addAttribute("error", e.getMessage());
            return "perfil";
        }

        // Actualizar la sesión con los nuevos datos
        session.setAttribute("usuarioLogueado", usuario);

        model.addAttribute("usuario", usuario);
        model.addAttribute("exito", "Datos actualizados correctamente.");
        return "perfil";
    }

    // ── POST /perfil/contrasena ───────────────────────────────────────────────

    @PostMapping("/perfil/contrasena")
    public String cambiarContrasena(@RequestParam String contrasenaActual,
                                    @RequestParam String contrasenaNueva,
                                    @RequestParam String confirmarNueva,
                                    HttpSession session,
                                    Model model) {

        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuario == null) return "redirect:/login";

        Usuario usuarioActualizado;
        try {
            perfilService.cambiarContrasena(usuario.getIdUsuario(), contrasenaActual, contrasenaNueva, confirmarNueva);
            usuarioActualizado = perfilService.obtenerPerfil(usuario.getIdUsuario());
        } catch (IllegalArgumentException e) {
            model.addAttribute("usuario", perfilService.obtenerPerfil(usuario.getIdUsuario()));
            model.addAttribute("error", e.getMessage());
            return "perfil";
        }

        session.setAttribute("usuarioLogueado", usuarioActualizado);
        model.addAttribute("usuario", usuarioActualizado);

        model.addAttribute("exito", "Contraseña actualizada correctamente.");
        return "perfil";
    }
}
