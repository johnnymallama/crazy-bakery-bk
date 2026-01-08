package uan.edu.co.crazy_bakery.infrastructure.web.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uan.edu.co.crazy_bakery.application.dto.requests.CrearIngredienteTamanoDTO;
import uan.edu.co.crazy_bakery.application.dto.responses.IngredienteTamanoDTO;
import uan.edu.co.crazy_bakery.application.services.IngredienteTamanoService;

import java.util.List;

@RestController
@RequestMapping("/ingrediente-tamano")
@RequiredArgsConstructor
public class IngredienteTamanoController {

    private final IngredienteTamanoService ingredienteTamanoService;

    @GetMapping("/{tamanoId}")
    public ResponseEntity<List<IngredienteTamanoDTO>> consultarPorTamano(@PathVariable Long tamanoId) {
        return ResponseEntity.ok(ingredienteTamanoService.consultarPorTamano(tamanoId));
    }

    @PostMapping
    public ResponseEntity<IngredienteTamanoDTO> crearRelacion(@RequestBody CrearIngredienteTamanoDTO crearIngredienteTamanoDTO) {
        return new ResponseEntity<>(ingredienteTamanoService.crearRelacion(crearIngredienteTamanoDTO), HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> inactivarRelacion(@PathVariable Long id) {
        if (ingredienteTamanoService.inactivarRelacion(id)) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
