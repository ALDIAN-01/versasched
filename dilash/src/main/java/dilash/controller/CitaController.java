package dilash.controller;

import dilash.service.CitaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * CitaController (REST) — eliminado el endpoint de prueba.
 *
 * CAMBIOS VS. VERSIÓN ANTERIOR:
 *  - Eliminado POST /citas/crear (creaba una Cita con campos que ya no existen).
 *  - Añadido GET /citas/todas para que el Admin pueda consumir el SP desde JS
 *    si en el futuro se requiere (actualmente admin.html lo carga via Thymeleaf
 *    en VistaController, pero este endpoint queda disponible).
 */
@RestController
@RequestMapping("/citas")
public class CitaController {

    @Autowired
    private CitaService citaService;

    /**
     * Endpoint REST opcional: devuelve todas las citas usando sp_consultar_todas_las_citas.
     * Útil si en el futuro el front-end Admin consume datos vía fetch/AJAX.
     */
    @GetMapping("/todas")
    public List<Map<String, Object>> todasLasCitas() {
        return citaService.consultarTodasLasCitas();
    }
}
