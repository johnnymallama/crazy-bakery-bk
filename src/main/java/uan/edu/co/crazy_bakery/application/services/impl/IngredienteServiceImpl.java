package uan.edu.co.crazy_bakery.application.services.impl;

import org.springframework.stereotype.Service;
import uan.edu.co.crazy_bakery.application.dto.requests.CrearIngredienteDTO;
import uan.edu.co.crazy_bakery.application.dto.responses.IngredienteDTO;
import uan.edu.co.crazy_bakery.application.mappers.IngredienteMapper;
import uan.edu.co.crazy_bakery.application.services.IngredienteService;
import uan.edu.co.crazy_bakery.domain.enums.TipoIngrediente;
import uan.edu.co.crazy_bakery.domain.model.Ingrediente;
import uan.edu.co.crazy_bakery.infrastructure.repositories.IngredienteRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class IngredienteServiceImpl implements IngredienteService {

    private final IngredienteRepository ingredienteRepository;
    private final IngredienteMapper ingredienteMapper;

    public IngredienteServiceImpl(IngredienteRepository ingredienteRepository, IngredienteMapper ingredienteMapper) {
        this.ingredienteRepository = ingredienteRepository;
        this.ingredienteMapper = ingredienteMapper;
    }

    @Override
    public IngredienteDTO createIngrediente(CrearIngredienteDTO crearIngredienteDTO) {
        Ingrediente ingrediente = ingredienteMapper.crearIngredienteDTOToIngrediente(crearIngredienteDTO);
        ingrediente.setEstado(true);
        ingrediente = ingredienteRepository.save(ingrediente);
        return ingredienteMapper.ingredienteToIngredienteDTO(ingrediente);
    }

    @Override
    public Optional<IngredienteDTO> getIngrediente(Long id) {
        return ingredienteRepository.findById(id).map(ingredienteMapper::ingredienteToIngredienteDTO);
    }

    @Override
    public List<IngredienteDTO> getAllIngredientes() {
        return ingredienteRepository.findAllByEstado(true)
                .stream()
                .map(ingredienteMapper::ingredienteToIngredienteDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<IngredienteDTO> updateIngrediente(Long id, CrearIngredienteDTO crearIngredienteDTO) {
        return ingredienteRepository.findById(id)
                .map(ingrediente -> {
                    ingrediente.setNombre(crearIngredienteDTO.getNombre());
                    ingrediente.setComposicion(crearIngredienteDTO.getComposicion());
                    ingrediente.setTipoIngrediente(crearIngredienteDTO.getTipoIngrediente());
                    ingrediente.setCostoPorGramo(crearIngredienteDTO.getCostoPorGramo());
                    ingrediente = ingredienteRepository.save(ingrediente);
                    return ingredienteMapper.ingredienteToIngredienteDTO(ingrediente);
                });
    }

    @Override
    public Optional<IngredienteDTO> deactivateIngrediente(Long id) {
        return ingredienteRepository.findById(id)
                .map(ingrediente -> {
                    ingrediente.setEstado(false);
                    ingrediente = ingredienteRepository.save(ingrediente);
                    return ingredienteMapper.ingredienteToIngredienteDTO(ingrediente);
                });
    }

    @Override
    public List<IngredienteDTO> findByTipoIngrediente(TipoIngrediente tipoIngrediente) {
        return ingredienteRepository.findByTipoIngredienteAndEstado(tipoIngrediente, true)
                .stream()
                .map(ingredienteMapper::ingredienteToIngredienteDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<IngredienteDTO> searchIngredientes(String tipoReceta, Long tamanoId, String tipoIngrediente) {
        List<Ingrediente> ingredientes = ingredienteRepository.searchIngredientes(tipoReceta, tamanoId, tipoIngrediente);
        return ingredientes.stream()
                .map(ingredienteMapper::ingredienteToIngredienteDTO)
                .collect(Collectors.toList());
    }
}
