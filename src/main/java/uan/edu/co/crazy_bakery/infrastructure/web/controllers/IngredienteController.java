package uan.edu.co.crazy_bakery.infrastructure.web.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uan.edu.co.crazy_bakery.application.dto.requests.CrearIngredienteDTO;
import uan.edu.co.crazy_bakery.application.dto.responses.IngredienteDTO;
import uan.edu.co.crazy_bakery.application.services.IngredienteService;

@RestController
@RequestMapping("/ingredientes")
public class IngredienteController {

    private final IngredienteService ingredienteService;

    public IngredienteController(IngredienteService ingredienteService) {
        this.ingredienteService = ingredienteService;
    }

    @PostMapping
    public ResponseEntity<IngredienteDTO> createIngrediente(@RequestBody CrearIngredienteDTO crearIngredienteDTO) {
        IngredienteDTO createdIngrediente = ingredienteService.createIngrediente(crearIngredienteDTO);
        return new ResponseEntity<>(createdIngrediente, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<IngredienteDTO> getIngrediente(@PathVariable Long id) {
        return ingredienteService.getIngrediente(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

}
