package dilash.service;

import dilash.model.Horario;
import dilash.repository.HorarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class HorarioService {

    @Autowired
    private HorarioRepository horarioRepository;

    public List<Horario> getTodos() {
        return horarioRepository.findAll();
    }

    public Optional<Horario> obtenerPorId(Long id) {
        return horarioRepository.findById(id);
    }
}
