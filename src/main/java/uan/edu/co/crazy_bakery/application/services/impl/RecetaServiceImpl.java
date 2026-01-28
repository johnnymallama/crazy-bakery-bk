package uan.edu.co.crazy_bakery.application.services.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import uan.edu.co.crazy_bakery.application.dto.requests.CrearRecetaDTO;
import uan.edu.co.crazy_bakery.application.dto.responses.RecetaDTO;
import uan.edu.co.crazy_bakery.application.mappers.RecetaMapper;
import uan.edu.co.crazy_bakery.application.services.RecetaService;
import uan.edu.co.crazy_bakery.domain.model.Receta;
import uan.edu.co.crazy_bakery.domain.model.Tamano;
import uan.edu.co.crazy_bakery.domain.model.Torta;
import uan.edu.co.crazy_bakery.infrastructure.repositories.RecetaRepository;
import uan.edu.co.crazy_bakery.infrastructure.repositories.TortaRepository;

@Service
public class RecetaServiceImpl implements RecetaService {

    private final RecetaRepository recetaRepository;
    private final TortaRepository tortaRepository;
    private final RecetaMapper recetaMapper;
    private final int costoManoObra;
    private final int costoOperativo;

    public RecetaServiceImpl(RecetaRepository recetaRepository,
                             TortaRepository tortaRepository,
                             RecetaMapper recetaMapper,
                             @Value("${cost.labor.value}") int costoManoObra,
                             @Value("${cost.operating.value}") int costoOperativo) {
        this.recetaRepository = recetaRepository;
        this.tortaRepository = tortaRepository;
        this.recetaMapper = recetaMapper;
        this.costoManoObra = costoManoObra;
        this.costoOperativo = costoOperativo;
    }

    @Override
    public RecetaDTO crearReceta(CrearRecetaDTO crearRecetaDTO) {
        Torta torta = tortaRepository.findById(crearRecetaDTO.getTortaId())
                .orElseThrow(() -> new RuntimeException("Torta no encontrada con id: " + crearRecetaDTO.getTortaId()));

        Receta receta = recetaMapper.crearRecetaDTOToReceta(crearRecetaDTO, torta);

        calcularCosto(torta, receta);
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

    private void calcularCosto(Torta torta, Receta receta) {
        Tamano tamano = torta.getTamano();
        float tiempoTamano = tamano.getTiempo();
        float costoManoObraTamano = this.costoManoObra * tiempoTamano;
        receta.setCostoManoObra(costoManoObraTamano);
        float costoOperativoTamano = this.costoOperativo * tiempoTamano;
        receta.setCostoOperativo(costoOperativoTamano);
    }
}
