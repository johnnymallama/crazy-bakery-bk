package uan.edu.co.crazy_bakery.infrastructure.web.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uan.edu.co.crazy_bakery.application.dto.requests.CrearIngredienteDTO;
import uan.edu.co.crazy_bakery.application.dto.responses.IngredienteDTO;
import uan.edu.co.crazy_bakery.application.services.IngredienteService;

import java.util.List;

@RestController
@RequestMapping("/ingredientes")
public class IngredienteController {

    private final IngredienteService ingredienteService;

    public IngredienteController(IngredienteService ingredienteService) {
        this.ingredienteService = ingredienteService;
    }

    @PostMapping
    public IngredienteDTO createIngrediente(@RequestBody CrearIngredienteDTO crearIngredienteDTO) {
        return ingredienteService.createIngrediente(crearIngredienteDTO);
    }

    @GetMapping("/{id}")
    public ResponseEntity<IngredienteDTO> getIngrediente(@PathVariable String id) {
        return ingredienteService.getIngrediente(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public List<IngredienteDTO> getAllIngredientes() {
        return ingredienteService.getAllIngredientes();
    }
}
