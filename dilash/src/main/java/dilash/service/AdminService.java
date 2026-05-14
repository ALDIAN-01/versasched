package dilash.service;

import dilash.model.Horario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
public class AdminService {

    @Autowired
    private CitaService citaService;

    @Autowired
    private HorarioService horarioService;

    public Map<String, Object> obtenerDatosPanel() {
        return citaService.obtenerPanelAdmin();
    }

    public void cambiarEstado(Integer idCita, Integer idEstado) {
        citaService.cambiarEstadoCita(idCita, idEstado);
    }

    public String reagendarCita(Integer idCita, String fecha, Long idHorario, String observaciones) {
        LocalDate nuevaFecha = LocalDate.parse(fecha);
        citaService.reagendarCita(idCita, nuevaFecha, idHorario, observaciones);
        Horario horario = horarioService.obtenerPorId(idHorario).orElse(null);
        return "Cita reagendada para el " + nuevaFecha + " a las " + (horario != null ? horario.getHora() : "?") + ".";
    }

    public void eliminarCita(Integer idCita) {
        citaService.eliminarCita(idCita);
    }
}
