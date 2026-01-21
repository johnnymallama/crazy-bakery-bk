package uan.edu.co.crazy_bakery.application.services.impl;

import lombok.AllArgsConstructor;

import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import uan.edu.co.crazy_bakery.application.dto.torta.CrearTortaDTO;
import uan.edu.co.crazy_bakery.application.dto.torta.TortaDTO;
import uan.edu.co.crazy_bakery.application.mappers.TortaMapper;
import uan.edu.co.crazy_bakery.application.services.TortaService;
import uan.edu.co.crazy_bakery.domain.model.Ingrediente;
import uan.edu.co.crazy_bakery.domain.model.IngredienteTamano;
import uan.edu.co.crazy_bakery.domain.model.Tamano;
import uan.edu.co.crazy_bakery.domain.model.Torta;
import uan.edu.co.crazy_bakery.domain.enums.TipoIngrediente;
import uan.edu.co.crazy_bakery.infrastructure.repositories.IngredienteRepository;
import uan.edu.co.crazy_bakery.infrastructure.repositories.IngredienteTamanoRepository;
import uan.edu.co.crazy_bakery.infrastructure.repositories.TamanoRepository;
import uan.edu.co.crazy_bakery.infrastructure.repositories.TortaRepository;
import java.util.List;

@Service
@AllArgsConstructor
public class TortaServiceImpl implements TortaService {

    private final TortaRepository tortaRepository;
    private final IngredienteRepository ingredienteRepository;
    private final TamanoRepository tamanoRepository;
    private final IngredienteTamanoRepository ingredienteTamanoRepository;
    private final TortaMapper tortaMapper;

    @Override
    public TortaDTO crearTorta(CrearTortaDTO crearTortaDTO) {
        Ingrediente bizcocho = ingredienteRepository.findById(crearTortaDTO.getBizcochoId())
                .orElseThrow(() -> new RuntimeException("Bizcocho no encontrado"));
        Ingrediente relleno = ingredienteRepository.findById(crearTortaDTO.getRellenoId())
                .orElseThrow(() -> new RuntimeException("Relleno no encontrado"));
        Ingrediente cubertura = ingredienteRepository.findById(crearTortaDTO.getCuberturaId())
                .orElseThrow(() -> new RuntimeException("Cubertura no encontrada"));
        Tamano tamano = tamanoRepository.findById(crearTortaDTO.getTamanoId())
                .orElseThrow(() -> new RuntimeException("Tamaño no encontrado"));

        float valor = calcularValor(bizcocho, relleno, cubertura, tamano);

        Torta torta = new Torta();
        torta.setBizcocho(bizcocho);
        torta.setRelleno(relleno);
        torta.setCubertura(cubertura);
        torta.setTamano(tamano);
        torta.setValor(valor);
        torta.setEstado(true);

        Torta tortaGuardada = tortaRepository.save(torta);

        return tortaMapper.toDTO(tortaGuardada);
    }

    @Override
    public List<TortaDTO> obtenerTodasLasTortas() {
        return tortaRepository.findAll().stream()
                .map(tortaMapper::toDTO)
                .collect(Collectors.toList());
    }

    private float calcularValor(Ingrediente bizcocho, Ingrediente relleno, Ingrediente cubertura, Tamano tamano) {
        float valorBizcocho = calcularCostoComponente(bizcocho, tamano, TipoIngrediente.BIZCOCHO);
        float valorRelleno = calcularCostoComponente(relleno, tamano, TipoIngrediente.RELLENO);
        float valorCubertura = calcularCostoComponente(cubertura, tamano, TipoIngrediente.COBERTURA);

        return valorBizcocho + valorRelleno + valorCubertura;
    }

    private float calcularCostoComponente(Ingrediente ingrediente, Tamano tamano, TipoIngrediente tipoIngrediente) {
        IngredienteTamano ingredienteTamano = ingredienteTamanoRepository.findByTamanoAndTipoIngredienteAndEstado(tamano, tipoIngrediente, true)
                .orElseThrow(() -> new RuntimeException("No se encontró la relación ingrediente-tamaño para " + tipoIngrediente));

        return ingrediente.getCostoPorGramo() * ingredienteTamano.getGramos();
    }
}
