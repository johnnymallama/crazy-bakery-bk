package uan.edu.co.crazy_bakery.infrastructure.web.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uan.edu.co.crazy_bakery.application.dto.requests.CrearIngredienteDTO;
import uan.edu.co.crazy_bakery.application.dto.responses.IngredienteDTO;
import uan.edu.co.crazy_bakery.application.services.IngredienteService;
import uan.edu.co.crazy_bakery.domain.enums.TipoIngrediente;

import java.util.List;

@Tag(name = "Ingredientes", description = "Gestión de ingredientes de la pastelería")
@RestController
@RequestMapping("/ingredientes")
public class IngredienteController {

    private final IngredienteService ingredienteService;

    public IngredienteController(IngredienteService ingredienteService) {
        this.ingredienteService = ingredienteService;
    }

    @Operation(summary = "Crear ingrediente", description = "Registra un nuevo ingrediente con su costo por gramo")
    @ApiResponse(responseCode = "201", description = "Ingrediente creado exitosamente")
    @PostMapping
    public ResponseEntity<IngredienteDTO> createIngrediente(@RequestBody CrearIngredienteDTO crearIngredienteDTO) {
        IngredienteDTO createdIngrediente = ingredienteService.createIngrediente(crearIngredienteDTO);
        return new ResponseEntity<>(createdIngrediente, HttpStatus.CREATED);
    }

    @Operation(summary = "Listar ingredientes", description = "Retorna todos los ingredientes activos")
    @ApiResponse(responseCode = "200", description = "Lista de ingredientes")
    @GetMapping
    public ResponseEntity<List<IngredienteDTO>> getAllIngredientes() {
        List<IngredienteDTO> ingredientes = ingredienteService.getAllIngredientes();
        return new ResponseEntity<>(ingredientes, HttpStatus.OK);
    }

    @Operation(summary = "Obtener ingrediente por ID", description = "Retorna un ingrediente por su ID. Devuelve 204 si está inactivo, 404 si no existe")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Ingrediente encontrado"),
        @ApiResponse(responseCode = "204", description = "Ingrediente inactivo"),
        @ApiResponse(responseCode = "404", description = "Ingrediente no encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<IngredienteDTO> getIngrediente(@Parameter(description = "ID del ingrediente") @PathVariable Long id) {
        return ingredienteService.getIngrediente(id)
                .map(dto -> {
                    if (!dto.isEstado()) {
                        return new ResponseEntity<IngredienteDTO>(HttpStatus.NO_CONTENT);
                    }
                    return new ResponseEntity<>(dto, HttpStatus.OK);
                })
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @Operation(summary = "Actualizar ingrediente", description = "Actualiza los datos de un ingrediente existente")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Ingrediente actualizado"),
        @ApiResponse(responseCode = "404", description = "Ingrediente no encontrado")
    })
    @PutMapping("/{id}")
    public ResponseEntity<IngredienteDTO> updateIngrediente(@Parameter(description = "ID del ingrediente") @PathVariable Long id, @RequestBody CrearIngredienteDTO crearIngredienteDTO) {
        return ingredienteService.updateIngrediente(id, crearIngredienteDTO)
                .map(ingrediente -> new ResponseEntity<>(ingrediente, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @Operation(summary = "Inactivar ingrediente", description = "Realiza una baja lógica del ingrediente (estado = false)")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Ingrediente inactivado"),
        @ApiResponse(responseCode = "404", description = "Ingrediente no encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<IngredienteDTO> deactivateIngrediente(@Parameter(description = "ID del ingrediente") @PathVariable Long id) {
        return ingredienteService.deactivateIngrediente(id)
                .map(ingrediente -> new ResponseEntity<>(ingrediente, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @Operation(summary = "Buscar por tipo de ingrediente", description = "Retorna todos los ingredientes activos de un tipo específico (BIZCOCHO, RELLENO, CUBERTURA)")
    @ApiResponse(responseCode = "200", description = "Lista de ingredientes del tipo indicado")
    @GetMapping("/tipo/{tipoIngrediente}")
    public ResponseEntity<List<IngredienteDTO>> findByTipoIngrediente(@Parameter(description = "Tipo de ingrediente") @PathVariable TipoIngrediente tipoIngrediente) {
        List<IngredienteDTO> ingredientes = ingredienteService.findByTipoIngrediente(tipoIngrediente);
        return new ResponseEntity<>(ingredientes, HttpStatus.OK);
    }

    @Operation(summary = "Buscar ingredientes compatibles", description = "Retorna ingredientes filtrados por tipo de receta, tamaño y tipo de ingrediente")
    @ApiResponse(responseCode = "200", description = "Lista de ingredientes filtrados")
    @GetMapping("/search")
    public ResponseEntity<List<IngredienteDTO>> searchIngredientes(
            @Parameter(description = "Tipo de receta (ej. TORTA)") @RequestParam String tipoReceta,
            @Parameter(description = "ID del tamaño") @RequestParam Long tamanoId,
            @Parameter(description = "Tipo de ingrediente") @RequestParam String tipoIngrediente) {
        List<IngredienteDTO> ingredientes = ingredienteService.searchIngredientes(tipoReceta, tamanoId, tipoIngrediente);
        return new ResponseEntity<>(ingredientes, HttpStatus.OK);
    }
}
