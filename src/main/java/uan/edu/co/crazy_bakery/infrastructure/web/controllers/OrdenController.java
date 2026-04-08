package uan.edu.co.crazy_bakery.infrastructure.web.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uan.edu.co.crazy_bakery.application.dto.requests.AgregarNotaOrdenDTO;
import uan.edu.co.crazy_bakery.application.dto.requests.AgregarRecetaOrdenDTO;
import uan.edu.co.crazy_bakery.application.dto.requests.CambiarEstadoOrdenDTO;
import uan.edu.co.crazy_bakery.application.dto.requests.CrearOrdenDTO;
import uan.edu.co.crazy_bakery.application.dto.responses.OrdenDTO;
import uan.edu.co.crazy_bakery.application.services.OrdenService;
import uan.edu.co.crazy_bakery.domain.enums.EstadoOrden;

import java.util.Date;
import java.util.List;

@Tag(name = "Órdenes", description = "Gestión del ciclo de vida de las órdenes de pedido")
@RestController
@RequestMapping("/orden")
@AllArgsConstructor
public class OrdenController {

    private final OrdenService ordenService;

    @Operation(summary = "Crear orden", description = "Registra una nueva orden de pedido para un cliente")
    @ApiResponse(responseCode = "201", description = "Orden creada exitosamente")
    @PostMapping
    public ResponseEntity<OrdenDTO> createOrden(@RequestBody CrearOrdenDTO crearOrdenDTO) {
        return new ResponseEntity<>(ordenService.createOrden(crearOrdenDTO), HttpStatus.CREATED);
    }

    @Operation(summary = "Listar todas las órdenes", description = "Retorna el historial de órdenes según el período configurado (HISTORY_MONTH)")
    @ApiResponse(responseCode = "200", description = "Lista de órdenes")
    @GetMapping
    public ResponseEntity<List<OrdenDTO>> getAllOrdenes() {
        return ResponseEntity.ok(ordenService.getAllOrdenes());
    }

    @Operation(summary = "Obtener orden por ID", description = "Retorna el detalle completo de una orden dado su ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Orden encontrada"),
        @ApiResponse(responseCode = "404", description = "Orden no encontrada")
    })
    @GetMapping("/{id}")
    public ResponseEntity<OrdenDTO> getOrdenById(@Parameter(description = "ID de la orden") @PathVariable Long id) {
        return ResponseEntity.ok(ordenService.getOrdenById(id));
    }

    @Operation(summary = "Listar órdenes por usuario", description = "Retorna todas las órdenes de un usuario identificado por su UID de Firebase")
    @ApiResponse(responseCode = "200", description = "Lista de órdenes del usuario")
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<OrdenDTO>> getOrdenesByUsuario(@Parameter(description = "UID del usuario (Firebase)") @PathVariable String usuarioId) {
        return ResponseEntity.ok(ordenService.getOrdenesByUsuario(usuarioId));
    }

    @Operation(summary = "Listar órdenes por estado", description = "Retorna órdenes filtradas por estado (PENDIENTE, EN_PROCESO, ENTREGADO, CANCELADO)")
    @ApiResponse(responseCode = "200", description = "Lista de órdenes con el estado indicado")
    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<OrdenDTO>> getOrdenesByEstado(@Parameter(description = "Estado de la orden") @PathVariable EstadoOrden estado) {
        return ResponseEntity.ok(ordenService.getOrdenesByEstado(estado));
    }

    @Operation(summary = "Listar órdenes por rango de fechas", description = "Retorna órdenes cuya fecha de creación está dentro del rango indicado (formato ISO: yyyy-MM-dd)")
    @ApiResponse(responseCode = "200", description = "Lista de órdenes en el rango de fechas")
    @GetMapping("/fecha")
    public ResponseEntity<List<OrdenDTO>> getOrdenesByFecha(
            @Parameter(description = "Fecha inicio (yyyy-MM-dd)") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date fechaInicio,
            @Parameter(description = "Fecha fin (yyyy-MM-dd)") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date fechaFin) {
        return ResponseEntity.ok(ordenService.getOrdenesByFecha(fechaInicio, fechaFin));
    }

    @Operation(summary = "Cambiar estado de una orden", description = "Actualiza el estado de la orden en su flujo: PENDIENTE → EN_PROCESO → ENTREGADO / CANCELADO")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Estado actualizado"),
        @ApiResponse(responseCode = "400", description = "Estado inválido"),
        @ApiResponse(responseCode = "404", description = "Orden no encontrada")
    })
    @PatchMapping("/{id}/estado")
    public ResponseEntity<OrdenDTO> cambiarEstadoOrden(
            @Parameter(description = "ID de la orden") @PathVariable Long id,
            @Valid @RequestBody CambiarEstadoOrdenDTO cambiarEstadoOrdenDTO) {
        OrdenDTO ordenActualizada = ordenService.cambiarEstadoOrden(id, cambiarEstadoOrdenDTO.getEstado());
        return ResponseEntity.ok(ordenActualizada);
    }

    @Operation(summary = "Agregar nota a una orden", description = "Adjunta una nota interna a la orden (instrucciones especiales, observaciones)")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Nota agregada"),
        @ApiResponse(responseCode = "404", description = "Orden no encontrada")
    })
    @PatchMapping("/{id}/nota")
    public ResponseEntity<OrdenDTO> agregarNotaOrden(
            @Parameter(description = "ID de la orden") @PathVariable Long id,
            @Valid @RequestBody AgregarNotaOrdenDTO agregarNotaOrdenDTO) {
        OrdenDTO ordenActualizada = ordenService.agregarNotaOrden(id, agregarNotaOrdenDTO);
        return ResponseEntity.ok(ordenActualizada);
    }

    @Operation(summary = "Agregar receta a una orden", description = "Vincula una receta (torta configurada) a la orden existente")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Receta agregada a la orden"),
        @ApiResponse(responseCode = "404", description = "Orden o receta no encontrada")
    })
    @PatchMapping("/{id}/receta")
    public ResponseEntity<OrdenDTO> agregarRecetaOrden(
            @Parameter(description = "ID de la orden") @PathVariable Long id,
            @Valid @RequestBody AgregarRecetaOrdenDTO agregarRecetaOrdenDTO) {
        OrdenDTO ordenActualizada = ordenService.agregarRecetaOrden(id, agregarRecetaOrdenDTO.getRecetaId());
        return ResponseEntity.ok(ordenActualizada);
    }
}
