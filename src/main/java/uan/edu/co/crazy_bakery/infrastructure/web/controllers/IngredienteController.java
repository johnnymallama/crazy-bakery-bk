package uan.edu.co.crazy_bakery.infrastructure.web.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uan.edu.co.crazy_bakery.application.dto.requests.CrearIngredienteDTO;
import uan.edu.co.crazy_bakery.application.dto.responses.IngredienteDTO;
import uan.edu.co.crazy_bakery.application.mappers.IngredienteMapper;
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
    public ResponseEntity<IngredienteDTO> createIngrediente(@RequestBody CrearIngredienteDTO crearIngredienteDTO) {
        IngredienteDTO createdIngrediente = ingredienteService.createIngrediente(crearIngredienteDTO);
        return new ResponseEntity<>(createdIngrediente, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getIngrediente(@PathVariable Long id) {
        return ingredienteService.getIngrediente(id)
                .map(ingrediente -> {
                    if (ingrediente.isEstado()) {
                        IngredienteDTO dto = IngredienteMapper.INSTANCE.ingredienteToIngredienteDTO(ingrediente);
                        return ResponseEntity.ok(dto);
                    } else {
                        return ResponseEntity.noContent().build();
                    }
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<IngredienteDTO>> getAllIngredientes() {
        List<IngredienteDTO> ingredientes = ingredienteService.getAllIngredientes();
        return new ResponseEntity<>(ingredientes, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<IngredienteDTO> updateIngrediente(@PathVariable Long id, @RequestBody CrearIngredienteDTO crearIngredienteDTO) {
        return ingredienteService.updateIngrediente(id, crearIngredienteDTO)
                .map(ingrediente -> new ResponseEntity<>(ingrediente, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<IngredienteDTO> deactivateIngrediente(@PathVariable Long id) {
        return ingredienteService.deactivateIngrediente(id)
                .map(ingrediente -> new ResponseEntity<>(ingrediente, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
}
