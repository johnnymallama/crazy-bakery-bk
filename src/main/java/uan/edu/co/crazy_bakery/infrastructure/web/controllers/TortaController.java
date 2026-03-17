package uan.edu.co.crazy_bakery.infrastructure.web.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uan.edu.co.crazy_bakery.application.dto.torta.CrearTortaDTO;
import uan.edu.co.crazy_bakery.application.dto.torta.TortaDTO;
import uan.edu.co.crazy_bakery.application.services.TortaService;

import java.util.List;

@Tag(name = "Tortas", description = "Gestión de tortas (combinación de bizcocho, relleno y cubertura)")
@RestController
@RequestMapping("/torta")
@AllArgsConstructor
public class TortaController {

    private final TortaService tortaService;

    @Operation(summary = "Crear torta", description = "Registra una nueva torta combinando un bizcocho, relleno y cubertura")
    @ApiResponse(responseCode = "201", description = "Torta creada exitosamente")
    @PostMapping
    public ResponseEntity<TortaDTO> crearTorta(@RequestBody CrearTortaDTO crearTortaDTO) {
        TortaDTO tortaCreada = tortaService.crearTorta(crearTortaDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(tortaCreada);
    }

    @Operation(summary = "Listar todas las tortas", description = "Retorna el catálogo completo de tortas disponibles")
    @ApiResponse(responseCode = "200", description = "Lista de tortas")
    @GetMapping
    public ResponseEntity<List<TortaDTO>> obtenerTodasLasTortas() {
        List<TortaDTO> tortas = tortaService.obtenerTodasLasTortas();
        return ResponseEntity.ok(tortas);
    }

}
