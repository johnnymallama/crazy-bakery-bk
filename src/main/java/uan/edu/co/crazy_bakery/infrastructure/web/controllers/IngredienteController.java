package uan.edu.co.crazy_bakery.infrastructure.web.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uan.edu.co.crazy_bakery.application.dto.requests.CrearIngredienteDTO;
import uan.edu.co.crazy_bakery.application.dto.responses.IngredienteDTO;
import uan.edu.co.crazy_bakery.application.services.IngredienteService;
import uan.edu.co.crazy_bakery.domain.enums.TipoIngrediente;

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
    public ResponseEntity<IngredienteDTO> getIngrediente(@PathVariable Long id) {
        return ingredienteService.getIngrediente(id)
                .map(dto -> {
                    if (!dto.isEstado()) {
                        return new ResponseEntity<IngredienteDTO>(HttpStatus.NO_CONTENT);
                    }
                    return new ResponseEntity<>(dto, HttpStatus.OK);
                })
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
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
