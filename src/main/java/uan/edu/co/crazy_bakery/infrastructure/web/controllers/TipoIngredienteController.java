package uan.edu.co.crazy_bakery.infrastructure.web.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uan.edu.co.crazy_bakery.application.services.TipoIngredienteService;
import uan.edu.co.crazy_bakery.domain.enums.TipoIngrediente;

import java.util.List;

@RestController
@RequestMapping("/tipo-ingrediente")
public class TipoIngredienteController {

    private final TipoIngredienteService tipoIngredienteService;

    public TipoIngredienteController(TipoIngredienteService tipoIngredienteService) {
        this.tipoIngredienteService = tipoIngredienteService;
    }

    @GetMapping
    public ResponseEntity<List<TipoIngrediente>> getAllTiposIngrediente() {
        List<TipoIngrediente> tipos = tipoIngredienteService.getAllTiposIngrediente();
        return new ResponseEntity<>(tipos, HttpStatus.OK);
    }
}
