package uan.edu.co.crazy_bakery.application.services;

import uan.edu.co.crazy_bakery.application.dto.requests.ActualizarTamanoDTO;
import uan.edu.co.crazy_bakery.application.dto.requests.CrearTamanoDTO;
import uan.edu.co.crazy_bakery.application.dto.responses.TamanoDTO;

import java.util.List;

public interface TamanoService {

    TamanoDTO crearTamano(CrearTamanoDTO crearTamanoDTO);

    TamanoDTO obtenerTamanoPorId(Long id);

    List<TamanoDTO> obtenerTodosLosTamanos();

    TamanoDTO actualizarTamano(Long id, ActualizarTamanoDTO actualizarTamanoDTO);

    void eliminarTamano(Long id);
}
