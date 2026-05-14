package dilash.controller;

import dilash.model.Usuario;
import dilash.service.UsuarioService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class RegistroController {

    @Autowired
    private UsuarioService usuarioService;

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

        try {
            usuarioService.registrarUsuario(nombre, apellido, correo, telefono, contrasena, tipoUsuario);
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "registro";
        }

        return "redirect:/login";
    }
}