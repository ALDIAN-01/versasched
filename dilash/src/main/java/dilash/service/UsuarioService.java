package dilash.service;

import dilash.model.Usuario;
import dilash.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public Usuario autenticar(String correo, String contrasena) {
        Usuario usuario = usuarioRepository.findByCorreo(correo);
        if (usuario == null) {
            return null;
        }

        String hashedPassword = usuario.getContrasena();
        if (hashedPassword == null) {
            return null;
        }

        if (passwordEncoder.matches(contrasena, hashedPassword)) {
            return usuario;
        }

        // Compatibilidad con contraseñas almacenadas en texto plano.
        if (hashedPassword.equals(contrasena)) {
            usuario.setContrasena(passwordEncoder.encode(contrasena));
            guardar(usuario);
            return usuario;
        }

        return null;
    }

    public Usuario findByCorreo(String correo) {
        return usuarioRepository.findByCorreo(correo);
    }

    public Optional<Usuario> findById(Long id) {
        return usuarioRepository.findById(id);
    }

    public boolean existePorCorreo(String correo) {
        return usuarioRepository.findByCorreo(correo) != null;
    }

    public Usuario guardar(Usuario usuario) {
        return usuarioRepository.save(usuario);
    }

    public Usuario registrarUsuario(String nombre, String apellido, String correo, String telefono, String contrasena, String tipoUsuario) {
        if (existePorCorreo(correo)) {
            throw new IllegalArgumentException("El correo ya está registrado");
        }

        Usuario usuario = new Usuario();
        usuario.setNombre(nombre);
        usuario.setApellido(apellido);
        usuario.setCorreo(correo);
        usuario.setTelefono(telefono);
        usuario.setContrasena(passwordEncoder.encode(contrasena));
        usuario.setTipoUsuario(tipoUsuario);

        return guardar(usuario);
    }

    public String determinarRedireccionPostLogin(Usuario usuario, String redirectParam) {
        if (redirectParam != null && !redirectParam.trim().isEmpty()) {
            return redirectParam;
        }
        return "ADMIN".equals(usuario.getTipoUsuario()) ? "/admin" : "/cliente";
    }
}
