package uan.edu.co.crazy_bakery.infrastructure.web.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uan.edu.co.crazy_bakery.application.dto.responses.geografia.CiudadDTO;
import uan.edu.co.crazy_bakery.application.dto.responses.geografia.DepartamentoDTO;
import uan.edu.co.crazy_bakery.application.services.GeografiaService;

import java.util.List;

@Tag(name = "Geografía", description = "Consulta de departamentos y ciudades de Colombia")
@RestController
@RequestMapping("/geografia")
public class GeografiaController {

    private final GeografiaService geografiaService;

    public GeografiaController(GeografiaService geografiaService) {
        this.geografiaService = geografiaService;
    }

    @Operation(summary = "Listar departamentos", description = "Retorna todos los departamentos disponibles")
    @ApiResponse(responseCode = "200", description = "Lista de departamentos")
    @GetMapping("/departamentos")
    public List<DepartamentoDTO> getDepartamentos() {
        return geografiaService.getDepartamentos();
    }

    @Operation(summary = "Listar ciudades", description = "Retorna todas las ciudades disponibles")
    @ApiResponse(responseCode = "200", description = "Lista de ciudades")
    @GetMapping("/ciudades")
    public List<CiudadDTO> getCiudades() {
        return geografiaService.getCiudades();
    }
}
