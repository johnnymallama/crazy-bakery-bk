package uan.edu.co.crazy_bakery.infrastructure.web.controllers;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uan.edu.co.crazy_bakery.application.dto.requests.CrearRecetaDTO;
import uan.edu.co.crazy_bakery.application.dto.responses.RecetaDTO;
import uan.edu.co.crazy_bakery.application.services.RecetaService;

@RestController
@RequestMapping("/receta")
@AllArgsConstructor
public class RecetaController {

    private final RecetaService recetaService;

    @PostMapping
    public ResponseEntity<RecetaDTO> crearReceta(@Valid @RequestBody CrearRecetaDTO crearRecetaDTO) {
        RecetaDTO nuevaReceta = recetaService.crearReceta(crearRecetaDTO);
        return new ResponseEntity<>(nuevaReceta, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RecetaDTO> obtenerRecetaPorId(@PathVariable Long id) {
        try {
            RecetaDTO receta = recetaService.obtenerRecetaPorId(id);
            return ResponseEntity.ok(receta);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
