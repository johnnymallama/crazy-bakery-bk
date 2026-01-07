package uan.edu.co.crazy_bakery.infrastructure.web.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uan.edu.co.crazy_bakery.application.dto.responses.geografia.CiudadDTO;
import uan.edu.co.crazy_bakery.application.dto.responses.geografia.DepartamentoDTO;
import uan.edu.co.crazy_bakery.application.services.GeografiaService;

import java.util.List;

@RestController
@RequestMapping("/geografia")
public class GeografiaController {

    private final GeografiaService geografiaService;

    public GeografiaController(GeografiaService geografiaService) {
        this.geografiaService = geografiaService;
    }

    @GetMapping("/departamentos")
    public List<DepartamentoDTO> getDepartamentos() {
        return geografiaService.getDepartamentos();
    }

    @GetMapping("/ciudades")
    public List<CiudadDTO> getCiudades() {
        return geografiaService.getCiudades();
    }
}
