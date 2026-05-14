package dilash.controller;

import dilash.service.CitaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * CitaController (REST API)
 *
 * Endpoints para consultar citas:
 * - GET /citas/todas: lista todas las citas (consumible por el panel admin)
 */
@RestController
@RequestMapping("/citas")
public class CitaController {

    @Autowired
    private CitaService citaService;

    /**
     * Obtiene todas las citas para el panel de administración (consumo vía JavaScript si es necesario).
     * Retorna JSON con: id_cita, cliente, fecha, hora, estado, total.
     */
    @GetMapping("/todas")
    public List<Map<String, Object>> todasLasCitas() {
        return citaService.consultarTodasLasCitas();
    }
}
