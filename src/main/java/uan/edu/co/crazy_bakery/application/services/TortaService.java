package uan.edu.co.crazy_bakery.application.services;

import uan.edu.co.crazy_bakery.application.dto.torta.CrearTortaDTO;
import uan.edu.co.crazy_bakery.application.dto.torta.TortaDTO;

import java.util.List;

public interface TortaService {

    TortaDTO crearTorta(CrearTortaDTO crearTortaDTO);

    List<TortaDTO> obtenerTodasLasTortas();

}
