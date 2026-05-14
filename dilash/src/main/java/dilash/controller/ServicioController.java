package dilash.controller;

import dilash.model.Servicio;
import dilash.service.ServicioService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class ServicioController {

    @Autowired
    private ServicioService servicioService;

    @GetMapping("/")
    public String listarServicios(Model model) {

        List<Servicio> servicios = servicioService.getTodos();

        model.addAttribute("servicios", servicios);

        return "index";
    }
}
