package dilash.service;

import dilash.model.Cita;
import dilash.model.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClienteService {

    @Autowired
    private CitaService citaService;

    public List<Cita> obtenerCitasPorUsuario(Usuario usuario) {
        return citaService.getCitasPorUsuario(usuario);
    }
}
