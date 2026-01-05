package uan.edu.co.crazy_bakery.application.services.impl;

import org.springframework.stereotype.Service;
import uan.edu.co.crazy_bakery.application.dto.requests.CrearIngredienteDTO;
import uan.edu.co.crazy_bakery.application.dto.responses.IngredienteDTO;
import uan.edu.co.crazy_bakery.application.mappers.IngredienteMapper;
import uan.edu.co.crazy_bakery.application.services.IngredienteService;
import uan.edu.co.crazy_bakery.domain.model.Ingrediente;
import uan.edu.co.crazy_bakery.infrastructure.repositories.IngredienteRepository;

import java.util.Optional;

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
        ingrediente = ingredienteRepository.save(ingrediente);
        return ingredienteMapper.ingredienteToIngredienteDTO(ingrediente);
    }

    @Override
    public Optional<IngredienteDTO> getIngrediente(Long id) {
        return ingredienteRepository.findById(id)
                .map(ingredienteMapper::ingredienteToIngredienteDTO);
    }
}
