package dilash.service;

import dilash.model.Usuario;
import dilash.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PerfilService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    public Usuario obtenerPerfil(Long idUsuario) {
        return usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado."));
    }

    public Usuario actualizarPerfil(Long idUsuario, String nombre, String apellido, String correo, String telefono) {
        Usuario usuario = obtenerPerfil(idUsuario);

        Usuario existente = usuarioRepository.findByCorreo(correo);
        if (existente != null && !existente.getIdUsuario().equals(idUsuario)) {
            throw new IllegalArgumentException("Ese correo ya está registrado por otra cuenta.");
        }

        usuario.setNombre(nombre.trim());
        usuario.setApellido(apellido.trim());
        usuario.setCorreo(correo.trim());
        usuario.setTelefono(telefono.trim());
        return usuarioRepository.save(usuario);
    }

    public void cambiarContrasena(Long idUsuario, String contrasenaActual, String contrasenaNueva, String confirmarNueva) {
        if (contrasenaNueva == null || contrasenaNueva.isBlank()) {
            throw new IllegalArgumentException("La nueva contraseña no puede estar vacía.");
        }
        if (!contrasenaNueva.equals(confirmarNueva)) {
            throw new IllegalArgumentException("La contraseña nueva y la confirmación no coinciden.");
        }

        Usuario usuario = obtenerPerfil(idUsuario);

        if (!usuario.getContrasena().equals(contrasenaActual)) {
            throw new IllegalArgumentException("La contraseña actual no es correcta.");
        }

        usuario.setContrasena(contrasenaNueva);
        usuarioRepository.save(usuario);
    }
}
