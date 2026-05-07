package uan.edu.co.crazy_bakery.application.services.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import uan.edu.co.crazy_bakery.application.dto.requests.CrearRecetaDTO;
import uan.edu.co.crazy_bakery.application.dto.responses.RecetaDTO;
import uan.edu.co.crazy_bakery.application.mappers.RecetaMapper;
import uan.edu.co.crazy_bakery.application.services.RecetaService;
import uan.edu.co.crazy_bakery.application.services.storage.StorageService;
import uan.edu.co.crazy_bakery.domain.model.Receta;
import uan.edu.co.crazy_bakery.domain.model.Tamano;
import uan.edu.co.crazy_bakery.domain.model.Torta;
import uan.edu.co.crazy_bakery.infrastructure.repositories.RecetaRepository;
import uan.edu.co.crazy_bakery.infrastructure.repositories.TortaRepository;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
public class RecetaServiceImpl implements RecetaService {

    private final RecetaRepository recetaRepository;
    private final TortaRepository tortaRepository;
    private final RecetaMapper recetaMapper;
    private final StorageService storageService;
    private final int costoManoObra;
    private final int costoOperativo;

    public RecetaServiceImpl(RecetaRepository recetaRepository,
                             TortaRepository tortaRepository,
                             RecetaMapper recetaMapper,
                             StorageService storageService,
                             @Value("${cost.labor.value}") int costoManoObra,
                             @Value("${cost.operating.value}") int costoOperativo) {
        this.recetaRepository = recetaRepository;
        this.tortaRepository = tortaRepository;
        this.recetaMapper = recetaMapper;
        this.storageService = storageService;
        this.costoManoObra = costoManoObra;
        this.costoOperativo = costoOperativo;
    }

    @Override
    public RecetaDTO crearReceta(CrearRecetaDTO crearRecetaDTO) {
        // Step: validar exista torta
        Torta torta = tortaRepository.findById(crearRecetaDTO.getTortaId())
                .orElseThrow(() -> new RuntimeException("Torta no encontrada con id: " + crearRecetaDTO.getTortaId()));

        // Step: hace mapper a entidad receta
        Receta receta = recetaMapper.crearRecetaDTOToReceta(crearRecetaDTO, torta);

        // Step: calcular costo
        calcularCosto(torta, receta);

        try {
            String sourceFileName = extractFileNameFromFirebaseUrl(crearRecetaDTO.getImagenUrl());
            String destFileName = "permanente/receta-" + System.currentTimeMillis() + ".jpg";
            String firebaseUrl = storageService.moveFile(sourceFileName, destFileName);
            receta.setImagenUrl(firebaseUrl);
        } catch (IOException e) {
            throw new RuntimeException("Error al mover la imagen al almacenamiento permanente", e);
        }

        // Step: establece estado en true
        receta.setEstado(true);

        // Step: Guarda receta
        Receta recetaGuardada = recetaRepository.save(receta);

        // Step: Responde con DTO Receta con la nueva URL
        return recetaMapper.recetaToRecetaDTO(recetaGuardada);
    }

    @Override
    public RecetaDTO obtenerRecetaPorId(Long id) {
        Receta receta = recetaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Receta no encontrada con id: " + id));
        return recetaMapper.recetaToRecetaDTO(receta);
    }

    @Override
    public List<String> getUltimasImagenes() {
        return recetaRepository.findUltimasImagenes();
    }

    private String extractFileNameFromFirebaseUrl(String firebaseUrl) {
        // URL format: https://firebasestorage.googleapis.com/v0/b/{bucket}/o/{encodedFileName}?alt=media
        String path = firebaseUrl.substring(firebaseUrl.indexOf("/o/") + 3);
        if (path.contains("?")) {
            path = path.substring(0, path.indexOf("?"));
        }
        return URLDecoder.decode(path, StandardCharsets.UTF_8);
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
