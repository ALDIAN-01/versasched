package dilash.controller;

import dilash.model.Servicio;
import dilash.repository.ServicioRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class ServicioController {

    @Autowired
    private ServicioRepository servicioRepository;

    @GetMapping("/")
    public String listarServicios(Model model) {

        List<Servicio> servicios = servicioRepository.findAll();

        model.addAttribute("servicios", servicios);

        return "index";
    }
}
