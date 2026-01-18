package uan.edu.co.crazy_bakery.infrastructure.web.controllers;

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

@RestController
@RequestMapping("/torta")
@AllArgsConstructor
public class TortaController {

    private final TortaService tortaService;

    @PostMapping
    public ResponseEntity<TortaDTO> crearTorta(@RequestBody CrearTortaDTO crearTortaDTO) {
        TortaDTO tortaCreada = tortaService.crearTorta(crearTortaDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(tortaCreada);
    }

    @GetMapping
    public ResponseEntity<List<TortaDTO>> obtenerTodasLasTortas() {
        List<TortaDTO> tortas = tortaService.obtenerTodasLasTortas();
        return ResponseEntity.ok(tortas);
    }

}
