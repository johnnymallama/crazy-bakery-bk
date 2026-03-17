package uan.edu.co.crazy_bakery.infrastructure.web.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uan.edu.co.crazy_bakery.application.dto.requests.CrearIngredienteTamanoDTO;
import uan.edu.co.crazy_bakery.application.dto.responses.IngredienteTamanoDTO;
import uan.edu.co.crazy_bakery.application.services.IngredienteTamanoService;

import java.util.List;

@Tag(name = "Ingrediente-Tamaño", description = "Relación entre ingredientes y tamaños de torta")
@RestController
@RequestMapping("/ingrediente-tamano")
@RequiredArgsConstructor
public class IngredienteTamanoController {

    private final IngredienteTamanoService ingredienteTamanoService;

    @Operation(summary = "Consultar ingredientes por tamaño", description = "Retorna todos los ingredientes asociados a un tamaño específico")
    @ApiResponse(responseCode = "200", description = "Lista de relaciones ingrediente-tamaño")
    @GetMapping("/{tamanoId}")
    public ResponseEntity<List<IngredienteTamanoDTO>> consultarPorTamano(@Parameter(description = "ID del tamaño") @PathVariable Long tamanoId) {
        return ResponseEntity.ok(ingredienteTamanoService.consultarPorTamano(tamanoId));
    }

    @Operation(summary = "Crear relación ingrediente-tamaño", description = "Asocia un ingrediente a un tamaño con la cantidad en gramos correspondiente")
    @ApiResponse(responseCode = "201", description = "Relación creada exitosamente")
    @PostMapping
    public ResponseEntity<IngredienteTamanoDTO> crearRelacion(@RequestBody CrearIngredienteTamanoDTO crearIngredienteTamanoDTO) {
        return new ResponseEntity<>(ingredienteTamanoService.crearRelacion(crearIngredienteTamanoDTO), HttpStatus.CREATED);
    }

    @Operation(summary = "Inactivar relación ingrediente-tamaño", description = "Realiza una baja lógica de la relación entre ingrediente y tamaño")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Relación inactivada exitosamente"),
        @ApiResponse(responseCode = "404", description = "Relación no encontrada")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> inactivarRelacion(@Parameter(description = "ID de la relación") @PathVariable Long id) {
        if (ingredienteTamanoService.inactivarRelacion(id)) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
