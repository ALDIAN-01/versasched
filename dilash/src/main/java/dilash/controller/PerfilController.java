package dilash.controller;

import dilash.model.Usuario;
import dilash.repository.UsuarioRepository;
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
    private UsuarioRepository usuarioRepository;

    // ── GET /perfil ───────────────────────────────────────────────────────────

    @GetMapping("/perfil")
    public String verPerfil(HttpSession session, Model model) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuario == null) return "redirect:/login";
        // Refrescar desde la BD para mostrar datos actualizados
        usuario = usuarioRepository.findById(usuario.getIdUsuario()).orElse(usuario);
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

        // Verificar que el correo no esté en uso por otro usuario
        Usuario existente = usuarioRepository.findByCorreo(correo);
        if (existente != null && !existente.getIdUsuario().equals(usuario.getIdUsuario())) {
            usuario = usuarioRepository.findById(usuario.getIdUsuario()).orElse(usuario);
            model.addAttribute("usuario", usuario);
            model.addAttribute("error", "Ese correo ya está registrado por otra cuenta.");
            return "perfil";
        }

        usuario.setNombre(nombre.trim());
        usuario.setApellido(apellido.trim());
        usuario.setCorreo(correo.trim());
        usuario.setTelefono(telefono.trim());
        usuarioRepository.save(usuario);

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

        usuario = usuarioRepository.findById(usuario.getIdUsuario()).orElse(usuario);
        model.addAttribute("usuario", usuario);

        // Verificar contraseña actual
        if (!usuario.getContrasena().equals(contrasenaActual)) {
            model.addAttribute("error", "La contraseña actual no es correcta.");
            return "perfil";
        }

        // Verificar que coincidan las nuevas
        if (!contrasenaNueva.equals(confirmarNueva)) {
            model.addAttribute("error", "Las contraseñas nuevas no coinciden.");
            return "perfil";
        }

        if (contrasenaNueva.length() < 6) {
            model.addAttribute("error", "La nueva contraseña debe tener al menos 6 caracteres.");
            return "perfil";
        }

        usuario.setContrasena(contrasenaNueva);
        usuarioRepository.save(usuario);
        session.setAttribute("usuarioLogueado", usuario);

        model.addAttribute("exito", "Contraseña actualizada correctamente.");
        return "perfil";
    }
}
