package uan.edu.co.crazy_bakery.infrastructure.web.controllers;

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

@RestController
@RequestMapping("/orden")
@AllArgsConstructor
public class OrdenController {

    private final OrdenService ordenService;

    @PostMapping
    public ResponseEntity<OrdenDTO> createOrden(@RequestBody CrearOrdenDTO crearOrdenDTO) {
        return new ResponseEntity<>(ordenService.createOrden(crearOrdenDTO), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<OrdenDTO>> getAllOrdenes() {
        return ResponseEntity.ok(ordenService.getAllOrdenes());
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrdenDTO> getOrdenById(@PathVariable Long id) {
        return ResponseEntity.ok(ordenService.getOrdenById(id));
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<OrdenDTO>> getOrdenesByUsuario(@PathVariable String usuarioId) {
        return ResponseEntity.ok(ordenService.getOrdenesByUsuario(usuarioId));
    }

    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<OrdenDTO>> getOrdenesByEstado(@PathVariable EstadoOrden estado) {
        return ResponseEntity.ok(ordenService.getOrdenesByEstado(estado));
    }

    @GetMapping("/fecha")
    public ResponseEntity<List<OrdenDTO>> getOrdenesByFecha(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date fechaFin) {
        return ResponseEntity.ok(ordenService.getOrdenesByFecha(fechaInicio, fechaFin));
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<OrdenDTO> cambiarEstadoOrden(
            @PathVariable Long id,
            @Valid @RequestBody CambiarEstadoOrdenDTO cambiarEstadoOrdenDTO) {
        OrdenDTO ordenActualizada = ordenService.cambiarEstadoOrden(id, cambiarEstadoOrdenDTO.getEstado());
        return ResponseEntity.ok(ordenActualizada);
    }

    @PatchMapping("/{id}/nota")
    public ResponseEntity<OrdenDTO> agregarNotaOrden(
            @PathVariable Long id,
            @Valid @RequestBody AgregarNotaOrdenDTO agregarNotaOrdenDTO) {
        OrdenDTO ordenActualizada = ordenService.agregarNotaOrden(id, agregarNotaOrdenDTO);
        return ResponseEntity.ok(ordenActualizada);
    }

    @PatchMapping("/{id}/receta")
    public ResponseEntity<OrdenDTO> agregarRecetaOrden(
            @PathVariable Long id,
            @Valid @RequestBody AgregarRecetaOrdenDTO agregarRecetaOrdenDTO) {
        OrdenDTO ordenActualizada = ordenService.agregarRecetaOrden(id, agregarRecetaOrdenDTO.getRecetaId());
        return ResponseEntity.ok(ordenActualizada);
    }
}
