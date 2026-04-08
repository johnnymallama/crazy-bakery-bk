package uan.edu.co.crazy_bakery.infrastructure.web.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uan.edu.co.crazy_bakery.application.dto.requests.ActualizarTamanoDTO;
import uan.edu.co.crazy_bakery.application.dto.requests.CrearTamanoDTO;
import uan.edu.co.crazy_bakery.application.dto.responses.TamanoDTO;
import uan.edu.co.crazy_bakery.application.services.TamanoService;
import uan.edu.co.crazy_bakery.domain.enums.TipoReceta;

import java.util.List;

@Tag(name = "Tamaños", description = "Gestión de tamaños de torta (porciones, dimensiones y gramos por ingrediente)")
@RestController
@RequestMapping("/tamanos")
public class TamanoController {

    private final TamanoService tamanoService;

    public TamanoController(TamanoService tamanoService) {
        this.tamanoService = tamanoService;
    }

    @Operation(summary = "Crear tamaño", description = "Registra un nuevo tamaño de torta con sus dimensiones, porciones y cantidades en gramos")
    @ApiResponse(responseCode = "201", description = "Tamaño creado exitosamente")
    @PostMapping
    public ResponseEntity<TamanoDTO> crearTamano(@RequestBody CrearTamanoDTO crearTamanoDTO) {
        TamanoDTO tamanoCreado = tamanoService.crearTamano(crearTamanoDTO);
        return new ResponseEntity<>(tamanoCreado, HttpStatus.CREATED);
    }

    @Operation(summary = "Obtener tamaño por ID", description = "Retorna los datos de un tamaño de torta dado su ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Tamaño encontrado"),
        @ApiResponse(responseCode = "404", description = "Tamaño no encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<TamanoDTO> obtenerTamanoPorId(@Parameter(description = "ID del tamaño") @PathVariable Long id) {
        TamanoDTO tamano = tamanoService.obtenerTamanoPorId(id);
        return ResponseEntity.ok(tamano);
    }

    @Operation(summary = "Listar todos los tamaños", description = "Retorna todos los tamaños de torta registrados en el sistema")
    @ApiResponse(responseCode = "200", description = "Lista de tamaños disponibles")
    @GetMapping
    public ResponseEntity<List<TamanoDTO>> obtenerTodosLosTamanos() {
        List<TamanoDTO> tamanos = tamanoService.obtenerTodosLosTamanos();
        return ResponseEntity.ok(tamanos);
    }

    @Operation(summary = "Actualizar tamaño", description = "Actualiza las dimensiones, porciones y tiempo de preparación de un tamaño existente")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Tamaño actualizado"),
        @ApiResponse(responseCode = "404", description = "Tamaño no encontrado")
    })
    @PutMapping("/{id}")
    public ResponseEntity<TamanoDTO> actualizarTamano(@Parameter(description = "ID del tamaño") @PathVariable Long id, @RequestBody ActualizarTamanoDTO actualizarTamanoDTO) {
        TamanoDTO tamanoActualizado = tamanoService.actualizarTamano(id, actualizarTamanoDTO);
        return ResponseEntity.ok(tamanoActualizado);
    }

    @Operation(summary = "Eliminar tamaño", description = "Elimina permanentemente un tamaño de torta")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Tamaño eliminado"),
        @ApiResponse(responseCode = "404", description = "Tamaño no encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarTamano(@Parameter(description = "ID del tamaño") @PathVariable Long id) {
        tamanoService.eliminarTamano(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Listar tamaños por tipo de receta", description = "Retorna los tamaños disponibles para un tipo de receta específico (ej. TORTA)")
    @ApiResponse(responseCode = "200", description = "Lista de tamaños del tipo de receta indicado")
    @GetMapping("/tipo-receta/{tipoReceta}")
    public ResponseEntity<List<TamanoDTO>> obtenerTamanosPorTipoReceta(@Parameter(description = "Tipo de receta") @PathVariable TipoReceta tipoReceta) {
        List<TamanoDTO> tamanos = tamanoService.obtenerTamanosPorTipoReceta(tipoReceta);
        return ResponseEntity.ok(tamanos);
    }
}
