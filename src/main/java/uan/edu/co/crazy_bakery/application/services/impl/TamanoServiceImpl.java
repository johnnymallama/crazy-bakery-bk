package uan.edu.co.crazy_bakery.application.services.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import uan.edu.co.crazy_bakery.application.dto.requests.ActualizarTamanoDTO;
import uan.edu.co.crazy_bakery.application.dto.requests.CrearTamanoDTO;
import uan.edu.co.crazy_bakery.application.dto.responses.TamanoDTO;
import uan.edu.co.crazy_bakery.application.mappers.TamanoMapper;
import uan.edu.co.crazy_bakery.application.services.TamanoService;
import uan.edu.co.crazy_bakery.domain.enums.TipoReceta;
import uan.edu.co.crazy_bakery.domain.model.Tamano;
import uan.edu.co.crazy_bakery.infrastructure.repositories.TamanoRepository;

import java.util.List;

@Service
@AllArgsConstructor
public class TamanoServiceImpl implements TamanoService {

    private final TamanoRepository tamanoRepository;
    private final TamanoMapper tamanoMapper;

    @Override
    public TamanoDTO crearTamano(CrearTamanoDTO crearTamanoDTO) {
        Tamano tamano = tamanoMapper.crearTamanoDTOToTamano(crearTamanoDTO);
        tamano.setEstado(true);
        Tamano tamanoGuardado = tamanoRepository.save(tamano);
        return tamanoMapper.tamanoToTamanoDTO(tamanoGuardado);
    }

    @Override
    public TamanoDTO obtenerTamanoPorId(Long id) {
        Tamano tamano = tamanoRepository.findByIdAndEstadoTrue(id)
                .orElseThrow(() -> new RuntimeException("Tamaño no encontrado con el id: " + id));
        return tamanoMapper.tamanoToTamanoDTO(tamano);
    }

    @Override
    public List<TamanoDTO> obtenerTodosLosTamanos() {
        List<Tamano> tamanos = tamanoRepository.findAllByEstadoTrue();
        return tamanoMapper.tamanosToTamanoDTOs(tamanos);
    }

    @Override
    public TamanoDTO actualizarTamano(Long id, ActualizarTamanoDTO actualizarTamanoDTO) {
        Tamano tamano = tamanoRepository.findByIdAndEstadoTrue(id)
                .orElseThrow(() -> new RuntimeException("Tamaño no encontrado con el id: " + id));
        tamano.setAlto(actualizarTamanoDTO.getAlto());
        tamano.setDiametro(actualizarTamanoDTO.getDiametro());
        tamano.setPorciones(actualizarTamanoDTO.getPorciones());
        Tamano tamanoActualizado = tamanoRepository.save(tamano);
        return tamanoMapper.tamanoToTamanoDTO(tamanoActualizado);
    }

    @Override
    public void eliminarTamano(Long id) {
        Tamano tamano = tamanoRepository.findByIdAndEstadoTrue(id)
                .orElseThrow(() -> new RuntimeException("Tamaño no encontrado con el id: " + id));
        tamano.setEstado(false);
        tamanoRepository.save(tamano);
    }

    @Override
    public List<TamanoDTO> obtenerTamanosPorTipoReceta(TipoReceta tipoReceta) {
        List<Tamano> tamanos = tamanoRepository.findAllByTipoRecetaAndEstadoTrue(tipoReceta);
        return tamanoMapper.tamanosToTamanoDTOs(tamanos);
    }
}
