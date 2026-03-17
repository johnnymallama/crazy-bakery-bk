package uan.edu.co.crazy_bakery.infrastructure.web.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uan.edu.co.crazy_bakery.application.dto.requests.CrearRecetaDTO;
import uan.edu.co.crazy_bakery.application.dto.responses.RecetaDTO;
import uan.edu.co.crazy_bakery.application.services.RecetaService;

import java.util.List;

@Tag(name = "Recetas", description = "Gestión de recetas: tortas configuradas con costos e imagen generada por IA")
@RestController
@RequestMapping("/receta")
@AllArgsConstructor
public class RecetaController {

    private final RecetaService recetaService;

    @Operation(summary = "Obtener últimas imágenes generadas", description = "Retorna las URLs de las imágenes de recetas generadas más recientemente")
    @ApiResponse(responseCode = "200", description = "Lista de URLs de imágenes")
    @GetMapping("/ultimas-imagenes")
    public ResponseEntity<List<String>> getUltimasImagenes() {
        List<String> imagenes = recetaService.getUltimasImagenes();
        return ResponseEntity.ok(imagenes);
    }

    @Operation(summary = "Crear receta", description = "Crea una nueva receta a partir de una torta y tamaño seleccionados. Calcula costos y genera imagen con DALL-E 3")
    @ApiResponse(responseCode = "201", description = "Receta creada con imagen generada y costos calculados")
    @PostMapping
    public ResponseEntity<RecetaDTO> crearReceta(@Valid @RequestBody CrearRecetaDTO crearRecetaDTO) {
        RecetaDTO nuevaReceta = recetaService.crearReceta(crearRecetaDTO);
        return new ResponseEntity<>(nuevaReceta, HttpStatus.CREATED);
    }

    @Operation(summary = "Obtener receta por ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Receta encontrada"),
        @ApiResponse(responseCode = "404", description = "Receta no encontrada")
    })
    @GetMapping("/{id}")
    public ResponseEntity<RecetaDTO> obtenerRecetaPorId(@Parameter(description = "ID de la receta") @PathVariable Long id) {
        try {
            RecetaDTO receta = recetaService.obtenerRecetaPorId(id);
            return ResponseEntity.ok(receta);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
