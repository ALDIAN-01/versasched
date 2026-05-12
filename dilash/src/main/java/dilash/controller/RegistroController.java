package dilash.controller;

import dilash.model.Usuario;
import dilash.repository.UsuarioRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class RegistroController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @GetMapping("/registro")
    public String mostrarRegistro() {
        return "registro";
    }

    @PostMapping("/registro")
    public String procesarRegistro(@RequestParam String nombre,
                                    @RequestParam String apellido,
                                    @RequestParam String correo,
                                    @RequestParam String telefono,
                                    @RequestParam String contrasena,
                                    @RequestParam String tipoUsuario,
                                    Model model) {

        // 🔒 Verificar si ya existe
        if (usuarioRepository.findByCorreo(correo) != null) {
            model.addAttribute("error", "El correo ya está registrado");
            return "registro";
        }

        Usuario usuario = new Usuario();
        usuario.setNombre(nombre);
        usuario.setApellido(apellido);
        usuario.setCorreo(correo);
        usuario.setTelefono(telefono);
        usuario.setContrasena(contrasena);
        usuario.setTipoUsuario(tipoUsuario);

        usuarioRepository.save(usuario);

        return "redirect:/login";
    }
}