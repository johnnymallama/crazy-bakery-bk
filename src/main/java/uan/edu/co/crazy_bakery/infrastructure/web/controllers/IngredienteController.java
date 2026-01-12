package uan.edu.co.crazy_bakery.infrastructure.web.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uan.edu.co.crazy_bakery.application.dto.requests.CrearIngredienteDTO;
import uan.edu.co.crazy_bakery.application.dto.responses.IngredienteDTO;
import uan.edu.co.crazy_bakery.application.mappers.IngredienteMapper;
import uan.edu.co.crazy_bakery.application.services.IngredienteService;
import uan.edu.co.crazy_bakery.domain.enums.TipoIngrediente;
import uan.edu.co.crazy_bakery.domain.model.Ingrediente;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/ingredientes")
public class IngredienteController {

    private final IngredienteService ingredienteService;
    private final IngredienteMapper ingredienteMapper;

    public IngredienteController(IngredienteService ingredienteService, IngredienteMapper ingredienteMapper) {
        this.ingredienteService = ingredienteService;
        this.ingredienteMapper = ingredienteMapper;
    }

    @PostMapping
    public ResponseEntity<IngredienteDTO> createIngrediente(@RequestBody CrearIngredienteDTO crearIngredienteDTO) {
        IngredienteDTO createdIngrediente = ingredienteService.createIngrediente(crearIngredienteDTO);
        return new ResponseEntity<>(createdIngrediente, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getIngrediente(@PathVariable Long id) {
        Optional<Ingrediente> ingredienteOpt = ingredienteService.getIngrediente(id);
        if (ingredienteOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Ingrediente ingrediente = ingredienteOpt.get();
        // If estado is null or false, it's considered inactive.
        if (ingrediente.getEstado() == null || !ingrediente.getEstado()) {
            return ResponseEntity.noContent().build();
        }

        // It's active, so map to DTO and return.
        IngredienteDTO dto = ingredienteMapper.ingredienteToIngredienteDTO(ingrediente);
        return ResponseEntity.ok(dto);
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

    @GetMapping("/tipo/{tipoIngrediente}")
    public ResponseEntity<List<IngredienteDTO>> findByTipoIngrediente(@PathVariable TipoIngrediente tipoIngrediente) {
        List<IngredienteDTO> ingredientes = ingredienteService.findByTipoIngrediente(tipoIngrediente);
        return new ResponseEntity<>(ingredientes, HttpStatus.OK);
    }
}
