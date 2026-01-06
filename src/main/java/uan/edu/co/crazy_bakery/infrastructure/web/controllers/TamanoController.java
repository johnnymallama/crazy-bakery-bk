package uan.edu.co.crazy_bakery.infrastructure.web.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uan.edu.co.crazy_bakery.application.dto.requests.ActualizarTamanoDTO;
import uan.edu.co.crazy_bakery.application.dto.requests.CrearTamanoDTO;
import uan.edu.co.crazy_bakery.application.dto.responses.TamanoDTO;
import uan.edu.co.crazy_bakery.application.services.TamanoService;

import java.util.List;

@RestController
@RequestMapping("/tamanos")
public class TamanoController {

    private final TamanoService tamanoService;

    public TamanoController(TamanoService tamanoService) {
        this.tamanoService = tamanoService;
    }

    @PostMapping
    public ResponseEntity<TamanoDTO> crearTamano(@RequestBody CrearTamanoDTO crearTamanoDTO) {
        TamanoDTO tamanoCreado = tamanoService.crearTamano(crearTamanoDTO);
        return new ResponseEntity<>(tamanoCreado, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TamanoDTO> obtenerTamanoPorId(@PathVariable Long id) {
        TamanoDTO tamano = tamanoService.obtenerTamanoPorId(id);
        return ResponseEntity.ok(tamano);
    }

    @GetMapping
    public ResponseEntity<List<TamanoDTO>> obtenerTodosLosTamanos() {
        List<TamanoDTO> tamanos = tamanoService.obtenerTodosLosTamanos();
        return ResponseEntity.ok(tamanos);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TamanoDTO> actualizarTamano(@PathVariable Long id, @RequestBody ActualizarTamanoDTO actualizarTamanoDTO) {
        TamanoDTO tamanoActualizado = tamanoService.actualizarTamano(id, actualizarTamanoDTO);
        return ResponseEntity.ok(tamanoActualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarTamano(@PathVariable Long id) {
        tamanoService.eliminarTamano(id);
        return ResponseEntity.noContent().build();
    }
}
