package uan.edu.co.crazy_bakery.application.services.impl;

import org.springframework.stereotype.Service;
import uan.edu.co.crazy_bakery.application.dto.requests.CrearIngredienteDTO;
import uan.edu.co.crazy_bakery.application.dto.responses.IngredienteDTO;
import uan.edu.co.crazy_bakery.application.mappers.IngredienteMapper;
import uan.edu.co.crazy_bakery.domain.model.Ingrediente;
import uan.edu.co.crazy_bakery.infrastructure.repositories.IngredienteRepository;
import uan.edu.co.crazy_bakery.application.services.IngredienteService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class IngredienteServiceImpl implements IngredienteService {

    private final IngredienteRepository ingredienteRepository;

    public IngredienteServiceImpl(IngredienteRepository ingredienteRepository) {
        this.ingredienteRepository = ingredienteRepository;
    }

    @Override
    public IngredienteDTO createIngrediente(CrearIngredienteDTO crearIngredienteDTO) {
        Ingrediente ingrediente = IngredienteMapper.INSTANCE.creaIngredienteDTOToIngrediente(crearIngredienteDTO);
        ingrediente = ingredienteRepository.save(ingrediente);
        return IngredienteMapper.INSTANCE.ingredienteToIngredienteDTO(ingrediente);
    }

    @Override
    public Optional<IngredienteDTO> getIngrediente(String id) {
        return ingredienteRepository.findById(id)
                .map(IngredienteMapper.INSTANCE::ingredienteToIngredienteDTO);
    }

    @Override
    public List<IngredienteDTO> getAllIngredientes() {
        return ingredienteRepository.findAll()
                .stream()
                .map(IngredienteMapper.INSTANCE::ingredienteToIngredienteDTO)
                .collect(Collectors.toList());
    }
}
