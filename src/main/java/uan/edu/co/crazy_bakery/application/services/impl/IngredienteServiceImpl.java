package uan.edu.co.crazy_bakery.application.services.impl;

import org.springframework.stereotype.Service;
import uan.edu.co.crazy_bakery.application.dto.requests.CrearIngredienteDTO;
import uan.edu.co.crazy_bakery.application.dto.responses.IngredienteDTO;
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

    public IngredienteServiceImpl(IngredienteRepository ingredienteRepository) {
        this.ingredienteRepository = ingredienteRepository;
    }

    private IngredienteDTO toDTO(Ingrediente ingrediente) {
        IngredienteDTO dto = new IngredienteDTO();
        dto.setId(ingrediente.getId());
        dto.setNombre(ingrediente.getNombre());
        dto.setComposicion(ingrediente.getComposicion());
        dto.setTipoIngrediente(ingrediente.getTipoIngrediente());
        dto.setValor(ingrediente.getValor());
        dto.setEstado(ingrediente.getEstado());
        return dto;
    }

    private Ingrediente toEntity(CrearIngredienteDTO crearIngredienteDTO) {
        Ingrediente ingrediente = new Ingrediente();
        ingrediente.setNombre(crearIngredienteDTO.getNombre());
        ingrediente.setComposicion(crearIngredienteDTO.getComposicion());
        ingrediente.setTipoIngrediente(crearIngredienteDTO.getTipoIngrediente());
        ingrediente.setValor(crearIngredienteDTO.getValor());
        return ingrediente;
    }

    @Override
    public IngredienteDTO createIngrediente(CrearIngredienteDTO crearIngredienteDTO) {
        Ingrediente ingrediente = toEntity(crearIngredienteDTO);
        ingrediente.setEstado(true);
        ingrediente = ingredienteRepository.save(ingrediente);
        return toDTO(ingrediente);
    }

    @Override
    public Optional<IngredienteDTO> getIngrediente(Long id) {
        return ingredienteRepository.findById(id).map(this::toDTO);
    }

    @Override
    public List<IngredienteDTO> getAllIngredientes() {
        return ingredienteRepository.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<IngredienteDTO> updateIngrediente(Long id, CrearIngredienteDTO crearIngredienteDTO) {
        return ingredienteRepository.findById(id)
                .map(ingrediente -> {
                    ingrediente.setNombre(crearIngredienteDTO.getNombre());
                    ingrediente.setComposicion(crearIngredienteDTO.getComposicion());
                    ingrediente.setTipoIngrediente(crearIngredienteDTO.getTipoIngrediente());
                    ingrediente.setValor(crearIngredienteDTO.getValor());
                    ingredienteRepository.save(ingrediente);
                    return toDTO(ingrediente);
                });
    }

    @Override
    public Optional<IngredienteDTO> deactivateIngrediente(Long id) {
        return ingredienteRepository.findById(id)
                .map(ingrediente -> {
                    ingrediente.setEstado(false);
                    ingredienteRepository.save(ingrediente);
                    return toDTO(ingrediente);
                });
    }

    @Override
    public List<IngredienteDTO> findByTipoIngrediente(TipoIngrediente tipoIngrediente) {
        return ingredienteRepository.findByTipoIngrediente(tipoIngrediente)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
}
