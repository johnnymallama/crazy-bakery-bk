package uan.edu.co.crazy_bakery.application.services.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import uan.edu.co.crazy_bakery.application.dto.requests.CrearRecetaDTO;
import uan.edu.co.crazy_bakery.application.dto.responses.RecetaDTO;
import uan.edu.co.crazy_bakery.application.mappers.RecetaMapper;
import uan.edu.co.crazy_bakery.application.services.RecetaService;
import uan.edu.co.crazy_bakery.domain.model.Receta;
import uan.edu.co.crazy_bakery.domain.model.Torta;
import uan.edu.co.crazy_bakery.infrastructure.repositories.RecetaRepository;
import uan.edu.co.crazy_bakery.infrastructure.repositories.TortaRepository;

@Service
@AllArgsConstructor
public class RecetaServiceImpl implements RecetaService {

    private final RecetaRepository recetaRepository;
    private final TortaRepository tortaRepository;
    private final RecetaMapper recetaMapper;

    @Override
    public RecetaDTO crearReceta(CrearRecetaDTO crearRecetaDTO) {
        Torta torta = tortaRepository.findById(crearRecetaDTO.getTortaId())
                .orElseThrow(() -> new RuntimeException("Torta no encontrada con id: " + crearRecetaDTO.getTortaId()));

        Receta receta = recetaMapper.crearRecetaDTOToReceta(crearRecetaDTO, torta);

        // Lógica para calcular el valor de la receta
        float valorReceta = torta.getValor() * receta.getCantidad();
        receta.setCostoTotal(valorReceta);
        receta.setEstado(true);

        Receta recetaGuardada = recetaRepository.save(receta);

        return recetaMapper.recetaToRecetaDTO(recetaGuardada);
    }

    @Override
    public RecetaDTO obtenerRecetaPorId(Long id) {
        Receta receta = recetaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Receta no encontrada con id: " + id));
        return recetaMapper.recetaToRecetaDTO(receta);
    }
}
