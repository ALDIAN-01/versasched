package dilash.service;

import dilash.model.EstadoCita;
import dilash.repository.EstadoCitaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EstadoCitaService {

    @Autowired
    private EstadoCitaRepository estadoCitaRepository;

    public List<EstadoCita> getTodos() {
        return estadoCitaRepository.findAll();
    }

    public Optional<EstadoCita> obtenerPorId(Integer id) {
        return estadoCitaRepository.findById(id);
    }
}
